/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.processes

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.stdlib.collapse.maxBy
import it.unibo.collektive.stdlib.time.sharedTimeLeftTo
import kotlin.math.max
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

typealias ReplicaID = ULong

/**
 * Represents a replicated state or process in a distributed system.
 * The `Replica` class models a computation that is associated with an identifier,
 * a state evolution function, a creation timestamp, and a specific time-to-live duration.
 *
 * **NB**: This class is intended for internal use only and should not be used directly;
 * it uses a non-serializable function type.
 * When inline functions support it, it will be moved as local class inside [timeReplicated].
 * @param Type The type of the resulting value computed by the process.
 * @property id A unique identifier for the replica.
 * @property process A computation expressed as an extension function on [Aggregate].
 */
@DelicateCollektiveApi
@PublishedApi
internal data class Replica<Type>(val id: ReplicaID, val process: () -> Type)

/**
 * A data class representing a process replica with an [id] and a [remainingTimeToLive].
 *
 * @property id The unique identifier of the timer replica.
 * @property remainingTimeToLive The remaining duration before the timer expires.
 */
@PublishedApi
@Serializable
internal data class TimedReplica(val id: ReplicaID, val remainingTimeToLive: Duration)

/**
 * A periodic restart strategy for removing obsolete information of [process] replicated across devices.
 * The process has a time-to-live of [timeToSpawn] and is replicated up to [maxReplicas] times.
 * If the time elapsed without a new replica being created is greater than [timeToSpawn],
 * the oldest replica is killed and a new one is created,
 * same if the number of replicas exceeds [maxReplicas].
 * The [process] function is executed on the replicas.
 */
@OptIn(DelicateCollektiveApi::class)
inline fun <reified Type : Any?> Aggregate<*>.timeReplicated(
    currentTime: Instant,
    maxReplicas: Int,
    timeToSpawn: Duration,
    noinline process: () -> Type,
): Type = evolving(emptyList<Replica<Type>>()) { localReplicas ->
    // the replica ID is either the id of the new replica to spawn or null if nothing new should spawn
    val baseReplicas = when (val newReplicaId = newReplicaID(timeToSpawn, currentTime)) {
        null -> localReplicas
        else -> localReplicas + Replica(id = newReplicaId, process = process)
    }
    val coercedReplicas = baseReplicas.drop(max(baseReplicas.size - maxReplicas, 0))
    check(coercedReplicas.isNotEmpty()) {
        "There should be at least one replica running after evaluating timeReplicated."
    }
    val oldestReplicaValue = coercedReplicas
        .map { alignedOn(it.id) { process() } }
        .first()
    coercedReplicas.yielding { oldestReplicaValue }
}

/**
 * Manages a shared timer across an aggregate network, creating or updating replicas
 * based on the time-to-live and the current time.
 *
 * @param timeToLive The duration for which a timer replica is valid.
 * @param currentTime The current timestamp used to evaluate the timer state.
 * @return The unique identifier of the newly created or updated timer replica.
 */
@PublishedApi
internal fun Aggregate<*>.newReplicaID(timeToLive: Duration, currentTime: Instant): ReplicaID? {
    val timeLeft = sharedTimeLeftTo(currentTime, timeToLive)
    return sharing(TimedReplica(0UL, ZERO)) { replicas ->
        val maxID = replicas.all.maxBy { it.value.id }.value.id
        when {
            maxID > replicas.local.value.id -> { // Someone else created a new replica, I need to follow it
                TimedReplica(maxID, timeToLive)
            }
            replicas.local.value.remainingTimeToLive <= ZERO -> { // I have to create a new replica
                TimedReplica(maxID.inc(), timeToLive)
            }
            else -> TimedReplica(replicas.local.value.id, timeLeft) // just update the time left
        }.yielding {
            id.takeUnless { it == replicas.local.value.id }
        }
    }
}
