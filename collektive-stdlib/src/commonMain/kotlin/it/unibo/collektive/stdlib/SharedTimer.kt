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
 * A shared timer progressing evenly across a network, at the pace set by the fastest device.
 * This function is useful to ensure that devices with drifting clocks or different round evaluation frequency
 * operate in a synchronized way.
 * The timer starts at [processTime] and decreases the [timeLeft] by [decay] every [step].
 * The timer is shared among all devices,
 * and it is alive for [timeLeft] units of time.
 */
// fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimer(
//    processTime: Instant,
//    timeLeft: Duration,
//    decay: Duration,
//    step: Duration,
// ): Duration =
//    share(processTime) { clock: Field<ID, Instant> ->
//        val clockPerceived: Instant = sharedClock(processTime, step)
//        val dt: Duration = deltaTime(processTime)
//        when {
//            dt >= step -> timeLeft - decay
//            else -> clockPerceived
//        }
//    }

//        if (clockPerceived <= clock.localValue) {
//             currently as fast as the fastest device in the neighborhood, so keep on counting time
//            clock.localValue + if (cyclicTimerWithDecay(timeLeft, processTime)) 1.toDuration(SECONDS) else ZERO
//        } else {
//            clockPerceived
//        }

/**
 * A cyclic timer that decays over time.
 * It starts from a [timeout] and decreases by [decayRate].
 * It returns `true` if the timer has completed a full cycle,
 * `false` otherwise.
 */
private fun <ID : Comparable<ID>> Aggregate<ID>.cyclicTimerWithDecay(timeout: Duration, decayRate: Duration): Boolean =
    evolve(timeout) { timer ->
        if (timer == ZERO) {
            timeout
        } else {
            countDownWithDecay(timeout, decayRate)
        }
    } == timeout

/**
 * A timer that decays over time.
 * It starts from a [timeout] and decreases by [decayRate].
 */
fun <ID : Comparable<ID>> Aggregate<ID>.countDownWithDecay(timeout: Duration, decayRate: Duration): Duration =
    timer(timeout, ZERO) { time -> time - decayRate }

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

fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(timeSensed: Instant): Instant {
    val localDelta: Duration = localDeltaTime(timeSensed)
    return share(DISTANT_PAST) { clocksAround: Field<ID, Instant> ->
        val localClockWithDelta = clocksAround.local.value + localDelta
        maxOf(localClockWithDelta, clocksAround.all.maxBy { it.value }.value)
    }
}

/**
 * A shared clock across a network at the pace set by the fastest device.
 * Starts from an initial value that is the [current] time of execution of the device
 * and returns the [Instant] of the fastest device.
 *
 * **N.B.**: [current] is set as default to the current system time,
 * but it is recommended to change it according to the requirements to achieve accurate and non-astonishing results.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedClockWithLag(current: Instant): Instant {
    val nbrLag = neighboringLag(current) // time elapsed from neighbors to the current device
    return share(DISTANT_PAST) { clocksAround: Field<ID, Instant> ->
        clocksAround.alignedMap(nbrLag) { _, base, dt -> base + dt }.all.maxBy { it.value }.value
    }
}

// fun <ID : Comparable<ID>> Aggregate<ID>. sharedTimeElapsed(deltaTime: Instant): Duration =
//    share(deltaTime) { time ->
// //        time.map { timeElapsed(it) }.max(base = ZERO)
//    }
//

// fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(current: Instant, interval: Duration): Instant =
//    share(current) { clock ->
//        val dt = deltaTime(current)è an
//        when {
//            dt >= interval -> clock.max(base = clock.localValue)
//            else -> clock.localValue
//        }
//    }
