/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.api.Aggregate
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO
import kotlin.time.ExperimentalTime

/**
 * A periodic restart strategy for removing obsolete information of [process] replicated across devices.
 * The process has a time-to-live of [timeToLive] and is replicated up to [maxReplicas] times.
 * If the time elapsed without a new replica being created is greater than [timeToLive],
 * the oldest replica is killed and a new one is created,
 * same if the number of replicas exceeds [maxReplicas].
 * The [process] function is executed on the replicas.
 * The [default] value is returned if no replica is alive.
 */
fun <ID : Comparable<ID>, Type : Any> Aggregate<ID>.timeReplicated(
    default: Type,
//    now: Instant,
//    timeToLive: Duration,
//    maxReplicas: Int,
//    decay: Duration,
//    step: Duration,
//    process: Aggregate<ID>.() -> Type,
): Type {
//     time elapsed without a new replica being created
//    val deltaTime: Duration = localDeltaTime(now)
//    val timeElapsed = sharedTimer(now, timeToLive, decay, step)
//    val result = evolve(emptyList<Replica<ID, Type>>()) { replicas ->
// //         kill the oldest one if there are more than maxReplicas, or if enough time has passed
//        val applyReplicas = when {
//            replicas.isEmpty() -> listOf(Replica(0u, process, DISTANT_PAST, ZERO))
//            else -> {
//                  TODO maybe in this way I have only the max from my neighborhood?
//                val maxID = replicas.maxBy { it.id }.id
//                val oldest = replicas.minBy { r -> r.creationTime }
//                val oldestDT = localDeltaTime(oldest.creationTime)
//                when {
//                    oldestDT >= timeToLive || replicas.size == maxReplicas ->
//                      replicas.filter { it.id == oldest.id } + Replica(maxID + 1u, process, DISTANT_PAST, timeElapsed)
//                    else ->
//                        when {
//                            timeElapsed > ZERO -> replicas + Replica(maxID + 1u, process, DISTANT_PAST, timeElapsed)
//                            else -> replicas
//                        }
//                }
//            }
//        }
//        applyReplicas.forEach {
//            alignedOn(it.id) {
//                it.process(this@timeReplicated)
//            }
//        }
//        applyReplicas
//    }
//    return result.firstOrNull()?.process?.let { it() } ?: default
    return default
}

/**
 * Represents a replicated state or process in a distributed system.
 *
 * The `Replica` class models a computation that is associated with an identifier,
 * a state evolution function, a creation timestamp, and a specific time-to-live duration.
 *
 * @param ID The type of the identifier used for the replicated entity. Must be comparable.
 * @param Type The type of the resulting value computed by the process.
 * @property id A unique identifier for the replica.
 * @property process A computation expressed as an extension function on [Aggregate].
 * This function defines the computation logic to be executed by the replica.
 * @property creationTime The time at which the replica is created.
 * @property timeToLive The duration after which the replica is considered expired.
 */
@OptIn(ExperimentalTime::class)
data class Replica<ID : Comparable<ID>, Type>(
    val id: ULong,
    val process: Aggregate<ID>.() -> Type,
    val creationTime: Instant,
    val timeToLive: Duration,
)
