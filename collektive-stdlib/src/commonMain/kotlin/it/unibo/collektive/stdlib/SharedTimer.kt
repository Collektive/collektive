/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.stdlib.collapse.maxBy
import it.unibo.collektive.stdlib.util.replaceMatching
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

/**
 * Checks if enough time has passed since the last recorded process time
 * to consider that the time-to-live has been exceeded.
 *
 * @param processTime The timestamp of the last recorded process time.
 * @param timeToWait The duration representing the time-to-live threshold.
 * @return `true` if the elapsed time since `processTime` exceeds `timeToLive`, `false` otherwise.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.enoughTimeHasPassed(
    processTime: Instant,
    timeToWait: Duration,
): Boolean {
    val clockPerceived: Instant = sharedClock(processTime)
    val timeElapsed = localDeltaTime(clockPerceived)
    return (timeToWait - timeElapsed) <= ZERO
}

/**
 * Calculates the time difference between the current moment (`now`) and a previous timestamp.
 * If no previous timestamp is present, the calculation assumes `DISTANT_PAST`.
 * The duration is always a non-negative value.
 *
 * @param now The current instant in time to compare against.
 * @return The duration between the `now` instant and the last stored timestamp in the aggregate.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.localDeltaTime(now: Instant): Duration =
    evolving(DISTANT_PAST) { previousTime ->
        val otherTime = if (previousTime == DISTANT_PAST) now else previousTime
        now.yielding { (now - otherTime).coerceAtLeast(ZERO) }
    }

/**
 * Computes the minimum delta time from the given [localDelta] value and the shared data context
 * across a network of devices within the aggregate program.
 *
 * This function uses the `sharing` mechanism to exchange stateful information and determines
 * the minimum delta time value from the locally provided `localDelta` and shared values from neighbors.
 *
 * @param localDelta The local delta time value to be considered as the basis for comparison.
 * @return The minimum delta time determined from the local and shared context values.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.minDelta(localDelta: Duration): Duration =
    sharing(localDelta) { deltaAround: Field<ID, Duration> ->
        val deltaReplaced = deltaAround.replaceMatching(localDelta) { it.value <= ZERO } // useless when local delta = 0
        // use neighbor's delta and add my new local delta; otherwise it would propagate the old (possibly wrong) delta
        // filtering out 0 to avoid blocking the clock, local delta if no other device has a valid delta
        val actualMin = (deltaReplaced.neighbors.list.map { it.value } + localDelta)
            .filter { it > ZERO }.minOrNull() ?: localDelta
        localDelta.yielding { actualMin } // propagate local, return the overall minimum
    }

/**
 * Computes the lag in time duration for each neighboring device relative to the current time.
 * The lag is zero for the local device and calculated as the difference between the current
 * time and the timestamp value of each neighbor.
 *
 * @param timeSensed The current timestamp as an [Instant].
 * @return A [Field] where each entry indicates the time lag ([Duration])
 *         for both the local device (set to zero) and neighboring devices.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.neighboringLag(timeSensed: Instant): Field<ID, Duration> =
    neighboring(timeSensed).map { timeSensed - it.value }

/**
 * A shared clock across a network at the pace set by the fastest device.
 * Starts from an initial value that is the [timeSensed] time of execution of the device
 * and returns the [Instant] of the fastest device.
 *
 * **N.B.**: [timeSensed] is set as default to the current system time,
 * but it is recommended to change it according to the requirements to achieve accurate and non-astonishing results.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedClockWithMinDelta(timeSensed: Instant): Instant {
    val localDelta: Duration = localDeltaTime(timeSensed)
    val minDelta = minDelta(localDelta)
    return share(DISTANT_PAST) { clocksAround: Field<ID, Instant> ->
        (clocksAround.all.maxBy { it.value }.value) + minDelta
    }
}

/**
 * Synchronizes the local clock of the aggregate with the maximum clock value among its neighbors,
 * ensuring that time does not move backward. The synchronization includes a local time adjustment
 * based on the sensed time delta.
 *
 * @param timeSensed The current timestamp sensed (used to calculate the local time difference).
 * @return The resulting synchronized timestamp for the local device.
 * @throws IllegalArgumentException if the local time delta is computed to be negative, indicating
 * that time has moved backward, which is not allowed.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(timeSensed: Instant): Instant {
    val localDelta: Duration = localDeltaTime(timeSensed)
    require(localDelta >= ZERO) { "Time has moved backwards. This should not happen." }
    return share(DISTANT_PAST) { clocksAround: Field<ID, Instant> ->
        val localClockWithDelta = clocksAround.local.value + localDelta
        maxOf(localClockWithDelta, clocksAround.all.maxBy { it.value }.value)
    }
}
