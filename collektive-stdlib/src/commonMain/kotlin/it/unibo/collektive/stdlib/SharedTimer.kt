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
import it.unibo.collektive.aggregate.api.neighborhood
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.stdlib.fields.max
import it.unibo.collektive.stdlib.fields.maxValue
import it.unibo.collektive.stdlib.fields.min
import it.unibo.collektive.stdlib.fields.minValue
import it.unibo.collektive.stdlib.fields.replaceMatching
import it.unibo.collektive.stdlib.pairs.FieldedPairs.component1
import it.unibo.collektive.stdlib.pairs.FieldedPairs.component2
import it.unibo.collektive.stdlib.util.IncludingSelf
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.ExperimentalTime

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
//@Serializable(with = InstantIso8601Serializer::class)
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

// fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(current: Instant = Clock.System.now(), interval: Duration): Instant =
//    share(current) { clock ->
//        val dt = deltaTime(current)è an
//        when {
//            dt >= interval -> clock.max(base = clock.localValue)
//            else -> clock.localValue
//        }
//    }

/**
 * Calculates the time difference between the current moment (`now`) and a previous timestamp.
 * If no previous timestamp is present, the calculation assumes `DISTANT_PAST`.
 * The duration is always a non-negative value.
 *
 * @param now The current instant in time to compare against.
 * @return The duration between the `now` instant and the last stored timestamp in the aggregate.
 */
@OptIn(ExperimentalTime::class)
fun <ID : Comparable<ID>> Aggregate<ID>.localDeltaTime(now: Instant): Duration = evolving(DISTANT_PAST) { previousTime ->
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
    sharing(ZERO) { deltaAround: Field<ID, Duration> ->
        println("DELTA AROUND: $deltaAround")
        val minDelta = deltaAround.replaceMatching(localDelta) { it.value <= ZERO }
            .min(IncludingSelf)
            ?.value ?: localDelta
        localDelta.yielding { minDelta }
    }

/**
 * A shared clock across a network at the pace set by the fastest device.
 * Starts from an initial value that is the [current] time of execution of the device
 * and returns the [Instant] of the fastest device.
 *
 * **N.B.**: [current] is set as default to the current system time,
 * but it is recommended to change it according to the requirements to achieve accurate and non-astonishing results.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(current: Instant): Instant {
    println("NOW FOR $localId is $current")
    val localDelta: Duration = localDeltaTime(current)
    println("LOCAL DELTA: $localDelta")
    println("device ${localId} neighbors: ${neighborhood().neighbors}")
    return share(DISTANT_PAST) { clocksAround: Field<ID, Instant> ->
        val minDelta: Duration = minDelta(localDelta)
        println("MIN DELTA: $minDelta")
        println("CLOCKS AROUND: $clocksAround")
        val nbrLag = neighboring(current).map { field ->
            if (field.id == localId) localDelta else current - field.value
        }
        println("NBR LAG: $nbrLag")
        val clocks = clocksAround.mapValues { it + minDelta }
        println("CLOCKS: $clocks")
        val res = clocksAround.alignedMap(nbrLag) { _, base, dt -> base + dt }.max(IncludingSelf)?.value ?: DISTANT_PAST
        println("res $res")
        clocks.max(IncludingSelf)!!.value
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
fun <ID : Comparable<ID>> Aggregate<ID>.clock(now: Instant): Instant {
    println("NOW FOR $localId is $now")
    val localDelta: Duration = localDeltaTime(now)
    println("LOCAL DELTA: $localDelta")

    return share(localDelta to DISTANT_PAST) { (deltaAround: Field<ID, Duration>, clocksAround: Field<ID, Instant>) ->
        println("deltaAround: $deltaAround")
        println("CLOCKSAROUND: $clocksAround")
//        check(localDelta > ZERO) { error("Time is not moving forward") }
        val deltaReplaced = deltaAround.replaceMatching(localDelta) { it.value <= ZERO }
        println("deltaReplaced: $deltaReplaced")
        val minDelta = deltaReplaced.min(IncludingSelf)!!.value
        println("MIN DELTA: $minDelta")
        val deltamins: Field<ID, Duration> = deltaReplaced.mapValues { it.coerceAtLeast(minDelta) }
        println("DELTAMINS: $deltamins")
        val maxTime = clocksAround.max(IncludingSelf)?.value ?: DISTANT_PAST
//        val referenceTime: Instant =
//            clocksAround.alignedMap<Duration, Instant>(deltamins) { _, base, dt -> base + dt }
//                .maxValue(clocksAround.local.value + (deltamins.minValue() ?: localDelta))
        println("REFERENCE TIME FOR DEVICE $localId: $maxTime")
        minDelta to (maxTime + minDelta)
    }.second
}

//fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(now: Instant): Instant =
//    share(now) { clocksAround: Field<ID, Instant> ->
//        val localDelta: Duration = deltaTime(now)
//        println("NOW FOR $localId is $now")
//        println("CLOCKSAROUND: $clocksAround")
////        check(localDelta > ZERO) { error("Time is not moving forward") }
//        val neighborsDelta: Field<ID, Duration> = clocksAround.mapValues { it - now } // passarlo a infinito
//        println("neighborsDelta: $neighborsDelta")
//        val deltaReplaced = neighborsDelta.replaceMatching(localDelta) { it.value < ZERO }
//        println("deltaReplaced: $deltaReplaced")
//        val minDelta: Duration = deltaReplaced.minBy(IncludingSelf) { it.value }?.value ?: localDelta // (localDelta)
//        println("MIN DELTA: $minDelta")
//        val deltamins: Field<ID, Duration> = neighborsDelta.mapValues { it.coerceAtLeast(minDelta) }
//        println("DELTAMINS: $deltamins")
//        val referenceTime: Instant =
//            clocksAround.alignedMap<Duration, Instant>(deltamins) { _, base, dt -> base + dt }
//                .maxValue(clocksAround.local.value + minDelta)
//        println("REFERENCE TIME FOR DEVICE $localId: $referenceTime")
//        referenceTime // + minDelta
//    }

//    return sharing(now to localDelta) { deltaAround: Field<ID, Pair<Instant, Duration>> ->
//        // take the minimum delta, i.e., the fastest device
//        val minDelta = deltaAround.minBy(deltaAround.localValue) { it.second }.secondà
//        // take the biggest time, i.e., the device most ahead
//        val fastestTime = deltaAround.maxBy(deltaAround.localValue) { it.first }.first
//        val newTime = fastestTime + minDelta
//        println("localDelta: $localDelta - minDelta: $minDelta - fastestTime: $fastestTime - newTime: $newTime")
//        (newTime to minDelta).yielding { newTime }
//    }

// shared clock -> Instant -> il device piu veloce sta a T

// fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimeElapsed(deltaTime: Instant): Duration =
//    share(deltaTime) { time ->
// //        time.map { timeElapsed(it) }.max(base = ZERO)
//    }
//

// @OptIn(ExperimentalTime::class)
// operator fun <ID: Comparable<ID>> Field<ID, Instant>.minus(other: Instant): Field<ID, Duration> =
//    this.mapValues { it.minus(other) }

/*
inline times_t shared_clock(node_t& node, trace_t call_point) {
    return nbr(node, call_point, times_t{0}, [&](field<times_t> x){
        return max_hood(node, call_point, node.previous_time() == TIME_MIN ? node.current_time() : x + node.nbr_lag());
    });
}
 */
