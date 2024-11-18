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
import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.operations.max
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlinx.datetime.plus
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

/**
 * A shared timer progressing evenly across a network, at the pace set by the fastest device.
 * This function is useful to ensure that devices with drifting clocks or different round evaluation frequency
 * operate in a synchronized way.
 * The timer starts at [ZERO] and increases by [deltaTime] every [processTime].
 * The timer is shared among all devices,
 * and it is alive for [timeToLive] units of time.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimer(timeToLive: Duration, processTime: Duration): Duration =
    share(ZERO) { clock: Field<ID, Duration> ->
        val clockPerceived = clock.max(base = ZERO)
        if (clockPerceived <= clock.localValue) {
            // currently as fast as the fastest device in the neighborhood, so keep on counting time
            clock.localValue + if (cyclicTimerWithDecay(timeToLive, processTime)) 1.toDuration(SECONDS) else ZERO
        } else {
            clockPerceived
        }
    }

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

// shared clock -> Instant -> il device piu veloce sta a T
// sharedTimeelapsed -> Duration -> sono passati deltaT secondi dal momento x

/**
 * A shared clock across a network at the pace set by the fastest device.
 * Returns the Instant of the fastest device.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.sharedClock(): Instant = share(Clock.System.now()) { clock ->
    clock.max(base = DISTANT_PAST)
}

/**
 *
 */
//fun <ID : Comparable<ID>> Aggregate<ID>.sharedTimeElapsed(deltaTime: Instant): Duration =
//    TODO("It has passed deltaT seconds from time x")
