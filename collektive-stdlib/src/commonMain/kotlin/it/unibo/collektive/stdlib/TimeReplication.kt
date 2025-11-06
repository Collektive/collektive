/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.api.Aggregate
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.time.Duration

/**
 * A periodic restart strategy for removing obsolete information of [process] replicated across devices.
 * The process has a time-to-live of [timeToSpawn] and is replicated up to [maxReplicas] times.
 * If the time elapsed without a new replica being created is greater than [timeToSpawn],
 * the oldest replica is killed and a new one is created,
 * same if the number of replicas exceeds [maxReplicas].
 * The [process] function is executed on the replicas.
 */
inline fun <reified Type : Any?> Aggregate<*>.timeReplicated(
    currentTime: Instant,
    maxReplicas: Int,
    timeToSpawn: Duration,
    noinline process: () -> Type,
): Type = evolving(emptySet<Replica<Type>>()) { localReplicas ->
    val localRep: MutableSet<Replica<Type>> = localReplicas.toMutableSet()
    val newReplicaId = sharedTimer(timeToSpawn, currentTime)
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
 *
 * The `Replica` class models a computation that is associated with an identifier,
 * a state evolution function, a creation timestamp, and a specific time-to-live duration.
 *
 * @param Type The type of the resulting value computed by the process.
 * @property id A unique identifier for the replica.
 * @property process A computation expressed as an extension function on [Aggregate].
 * This function defines the computation logic to be executed by the replica.
 */
@Serializable
data class Replica<Type>(val id: ReplicaID, val process: () -> Type) {
    override fun toString(): String = "Replica(id=$id)"
}
