/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.stdlib.collapse.maxBy
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

typealias ReplicaID = ULong

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
 * Manages a shared timer across an aggregate network, creating or updating replicas
 * based on the time-to-live and the current time.
 *
 * @param timeToLive The duration for which a timer replica is valid.
 * @param currentTime The current timestamp used to evaluate the timer state.
 * @return The unique identifier of the newly created or updated timer replica.
 */
fun Aggregate<*>.sharedTimer(timeToLive: Duration, currentTime: Instant): ReplicaID? {
    val timeLeft = sharedTimeLeftTo(currentTime, timeToLive)
    return sharing(TimerReplica(0UL, ZERO)) { replicas ->
        val maxID = replicas.all.maxBy { it.value.id }.value.id
        when {
            maxID > replicas.local.value.id -> { // Someone else created a new replica, I need to follow it
                TimerReplica(maxID, timeToLive)
            }
            replicas.local.value.remainingTimeToLive <= ZERO -> { // I have to create a new replica
                TimerReplica(maxID.inc(), timeToLive)
            }
            else -> TimerReplica(replicas.local.value.id, timeLeft) // just update the time left
        }.yielding {
            id.takeUnless { it == replicas.local.value.id }
        }
    }
}

/**
 * A data class representing a process replica with an [id] and a [remainingTimeToLive].
 *
 * @property id The unique identifier of the timer replica.
 * @property remainingTimeToLive The remaining duration before the timer expires.
 */
@PublishedApi
@Serializable
internal data class TimerReplica(val id: ReplicaID, val remainingTimeToLive: Duration)

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
    check(localDelta >= ZERO) { "Time has moved backwards. This should not happen." }
    return share(DISTANT_PAST) { clocksAround: Field<*, Instant> ->
        val localClockWithDelta = clocksAround.local.value + localDelta
        maxOf(localClockWithDelta, clocksAround.all.maxBy { it.value }.value)
    }
}
