/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
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
 * Computes the minimum delta time from the given [localDelta] value and the shared data context
 * across a network of devices within the aggregate program.
 *
 * This function uses the `sharing` mechanism to exchange stateful information and determines
 * the minimum delta time value from the locally provided `localDelta` and shared values from neighbors.
 *
 * @param localDelta The local delta time value to be considered as the basis for comparison.
 * @return The minimum delta time determined from the local and shared context values.
 */
fun Aggregate<*>.minDelta(localDelta: Duration): Duration = sharing(localDelta) { deltaAround: Field<*, Duration> ->
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
inline fun <reified ID : Comparable<ID>> Aggregate<ID>.neighboringLag(timeSensed: Instant): Field<ID, Duration> =
    neighboring(timeSensed).map { timeSensed - it.value }

/**
 * A shared clock across a network at the pace set by the fastest device.
 * Starts from an initial value that is the [timeSensed] time of execution of the device
 * and returns the [Instant] of the fastest device.
 *
 * **N.B.**: [timeSensed] is set as default to the current system time,
 * but it is recommended to change it according to the requirements to achieve accurate and non-astonishing results.
 */
inline fun <reified ID : Comparable<ID>> Aggregate<ID>.sharedClockWithMinDelta(timeSensed: Instant): Instant {
    val localDelta: Duration = TODO("localDeltaTime(timeSensed) is in branch feat/timeReplication, will be merged soon")
    val minDelta = minDelta(localDelta)
    return share(DISTANT_PAST) { clocksAround: Field<ID, Instant> ->
        (clocksAround.all.maxBy { it.value }.value) + minDelta
    }
}
