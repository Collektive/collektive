/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.max
import kotlinx.datetime.Instant
import kotlin.math.max
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
//fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimer(
//    processTime: Instant,
//    timeLeft: Duration,
//    decay: Duration,
//    step: Duration,
//): Duration =
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
 * A shared clock across a network at the pace set by the fastest device.
 * Starts from an initial value that is the [current] time of execution of the device
 * and returns the [Instant] of the fastest device.
 *
 * **N.B.**: [current] is set as default to the current system time,
 * but it is recommended to change it according to the requirements to achieve accurate and non-astonishing results.
 */
//fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(current: Instant = Clock.System.now(), interval: Duration): Instant =
//    share(current) { clock ->
//        val dt = deltaTime(current)
//        when {
//            dt >= interval -> clock.max(base = clock.localValue)
//            else -> clock.localValue
//        }
//    }

fun <ID : Comparable<ID>> Aggregate<ID>.deltaTime(now: Instant): Duration =
    evolving(now) { previousTime -> max(now, previousTime).yielding { (now - previousTime).coerceAtLeast(ZERO) } }


fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(now: Instant): Instant = share(now) { clocksAround: Field<ID, Instant> ->
//    val timeLocal = deltaTime()
    val deltaTimes = clocksAround.map { (now - it).coerceAtLeast(ZERO) }
    val referenceTime: Instant = (clocksAround.alignedMap(deltaTimes) { base, dt -> base + dt }).max(clocksAround.localValue)
    referenceTime + deltaTime
}

// shared clock -> Instant -> il device piu veloce sta a T

//fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimeElapsed(deltaTime: Instant): Duration =
//    share(deltaTime) { time ->
////        time.map { timeElapsed(it) }.max(base = ZERO)
//    }
