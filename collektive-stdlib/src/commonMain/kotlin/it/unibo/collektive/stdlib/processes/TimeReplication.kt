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
import it.unibo.collektive.stdlib.sharedTimeLeftTo
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

typealias ReplicaID = ULong

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
): Type = evolving(emptySet<Replica<Type>>()) { localReplicas ->
    val localRep: MutableSet<Replica<Type>> = localReplicas.toMutableSet()
    val newReplicaId = newReplicaID(timeToSpawn, currentTime)
    // if the replica ID is greater than the minimum, it means it is the ID of the new replica to be spawned
    if (newReplicaId != null) {
        if (localReplicas.size == maxReplicas) {
            val oldestReplica = localReplicas.minBy { it.id }
            localRep.remove(oldestReplica)
        }
        localRep.add(Replica(id = newReplicaId, process = process))
    }
    val oldestReplicaValue = localRep.map {
        alignedOn(it.id) {
            process()
        }
    }.firstOrNull()
        ?: error(
            "Empty replica set in timeReplicated, this should not happen, perhaps there is a bug, please report it.",
        )
    localRep.yielding { oldestReplicaValue }
}

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
