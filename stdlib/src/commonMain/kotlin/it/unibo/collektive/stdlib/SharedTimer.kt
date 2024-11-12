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
import it.unibo.collektive.field.operations.maxBy
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

/**
 * A shared timer to coordinate the execution of a process across multiple devices.
 * The timer starts at [ZERO] and increases by one unit every [processTime] units of time.
 * The timer is shared among all devices in the neighborhood,
 * and it is alive for [timeToLive] units of time.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimer(timeToLive: Duration, processTime: Duration): Duration {
    return share(ZERO) { clocks ->
        val clockPerceived = clocks.maxBy(clocks.localValue) { it }
        if (clockPerceived <= clocks.localValue) {
            // currently as fast as the fastest device in the neighborhood, so keep on counting time
            clocks.localValue + if (cyclicTimerWithDecay(timeToLive, processTime)) 1.toDuration(SECONDS) else ZERO
        } else {
            clockPerceived
        }
    }
}

/**
 * A cyclic timer that decays over time.
 * It starts from a [timeout] and decreases by [decayRate].
 * It returns `true` if the timer has completed a full cycle,
 * `false` otherwise.
 */
private fun <ID : Comparable<ID>> Aggregate<ID>.cyclicTimerWithDecay(timeout: Duration, decayRate: Duration): Boolean =
    repeat(timeout) { timer ->
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
