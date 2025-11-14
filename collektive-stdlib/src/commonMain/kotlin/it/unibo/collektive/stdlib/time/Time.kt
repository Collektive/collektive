/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.time

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.stdlib.collapse.maxBy
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

/**
 * Computes the time left (or past, when negative) until a timer expires,
 * using a shared clock across the aggregate network to evaluate elapsed time
 * (devices with faster clocks will drive the shared clock forward).
 *
 * @param now The locally-perceived device time.
 * @param timeToWait The duration representing the time-to-live threshold.
 * @return `true` if the elapsed time since `processTime` exceeds `timeToLive`, `false` otherwise.
 */
fun Aggregate<*>.sharedTimeLeftTo(now: Instant, timeToWait: Duration): Duration =
    timeToWait - localDeltaTime(sharedClock(now))

/**
 * Calculates the time difference between the current moment ([now]) and a previous timestamp.
 * The duration is always a non-negative value.
 *
 * @param now The current instant in time to compare against.
 * @return The duration between the `now` instant and the last stored timestamp in the aggregate.
 */
fun Aggregate<*>.localDeltaTime(now: Instant): Duration = evolving(now) { previousTime ->
    now.yielding { (now - previousTime).coerceAtLeast(ZERO) }
}

/**
 * Agrees on a shared clock timestamp across the aggregate network.
 * The device whose time progresses the most will drive the shared clock forward.
 *
 * @param localTime The time as perceived locally.
 * @return The resulting synchronized timestamp. The provided Instant will always be past or equal to the local time.
 * @throws IllegalArgumentException if the local time delta is computed to be negative, indicating
 * that time has moved backward.
 */
fun Aggregate<*>.sharedClock(localTime: Instant): Instant {
    val localDelta: Duration = localDeltaTime(localTime)
    check(localDelta >= ZERO) { "Time has moved backwards of $localDelta at $localTime." }
    return share(DISTANT_PAST) { clocksAround: Field<*, Instant> ->
        val localClockWithDelta = clocksAround.local.value + localDelta
        maxOf(localClockWithDelta, clocksAround.all.maxBy { it.value }.value)
    }
}
