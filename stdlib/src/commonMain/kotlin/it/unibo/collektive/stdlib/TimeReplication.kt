/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.api.Aggregate
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.Duration.Companion.ZERO

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
    now: Instant = Clock.System.now(),
    timeToLive: Duration,
    maxReplicas: Int,
    process: Aggregate<ID>.() -> Type,
): Type {
    // time elapsed without a new replica being created
    val deltaTime: Duration = evolving(now) { previousTime -> now.yielding { now - previousTime } }
    val timeElapsed = sharedTimer(timeToLive, deltaTime)
    val result = evolve(emptyList<Replica<ID, Type>>()) { replicas ->
        // kill the oldest one if there are more than maxReplicas, or if enough time has passed
        val applyReplicas = when {
            replicas.isEmpty() -> listOf(Replica(0u, process, ZERO))
            else -> {
                val maxID = replicas.maxBy { it.id }.id
                val oldest = replicas.maxBy { r -> r.timeAlive }
                when {
                    oldest.timeAlive >= timeToLive || replicas.size == maxReplicas ->
                        replicas.filter { it.id == oldest.id } + Replica(maxID + 1u, process, timeElapsed)
                    else ->
                        when {
                            timeElapsed > ZERO -> replicas + Replica(maxID + 1u, process, timeElapsed)
                            else -> replicas
                        }
                }
            }
        }
        applyReplicas.forEach {
            alignedOn(it.id) {
                it.process(this@timeReplicated)
            }
        }
        applyReplicas
    }
    return result.firstOrNull()?.process?.let { it() } ?: default
}

/**
 * A replica of a process that is alive for a certain amount of time [timeAlive].
 * It is identified by an [id] and runs the [process] function.
 * The [process] function is executed while the replica is alive.
 */
data class Replica<ID : Comparable<ID>, Type>(
    val id: ULong,
    val process: Aggregate<ID>.() -> Type,
    val timeAlive: Duration,
)
