/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.foldWithId

/**
 * Self-stabilizing gossip-max.
 * Spreads across all (aligned) devices the current maximum [Value] of [local],
 * as computed by [comparator].
 */
fun <ID : Comparable<ID>, Value> Aggregate<ID>.gossipMax(
    local: Value,
    comparator: Comparator<Value>,
): Value {
    /*
     * The best value exchanged in the gossip algorithm.
     * It contains the [best] value evaluated yet,
     * the [local] value of the node, and the [path] of nodes through which it has passed.
     */
    data class GossipValue<ID : Comparable<ID>, Value>(
        val best: Value,
        val local: Value,
        val path: List<ID> = emptyList(),
    ) {
        fun base(id: ID) = GossipValue(local, local, listOf(id))
    }

    val localGossip = GossipValue<ID, Value>(best = local, local = local)
    return share(localGossip) { gossip ->
        val neighbors = gossip.neighbors.toSet()
        val result =
            gossip.foldWithId(localGossip) { current, id, next ->
                val valid =
                    next.path
                        .asReversed()
                        .asSequence()
                        .drop(1)
                        .none { it == localId || it in neighbors }
                val actualNext = if (valid) next else next.base(id)
                val candidateValue = comparator.compare(current.best, actualNext.best)
                when {
                    candidateValue > 0 -> current
                    candidateValue == 0 -> listOf(current, next).minBy { it.path.size }
                    else -> actualNext
                }
            }
        GossipValue(result.best, local, result.path + localId)
    }.best
}

/**
 * Self-stabilizing [gossipMax] with a default comparator.
 * Spreads across all (aligned) devices the current maximum [Value] of [local],
 * as computed by first value compared to the second.
 */
fun <ID : Comparable<ID>, Value : Comparable<Value>> Aggregate<ID>.gossipMax(local: Value): Value =
    gossipMax(local) { first, second -> first.compareTo(second) }

/**
 * Self-stabilizing gossip-min.
 * Spreads across all (aligned) devices the current minimum [Value] of [local],
 * as computed by [comparator].
 */
fun <ID : Comparable<ID>, Value> Aggregate<ID>.gossipMin(
    local: Value,
    comparator: Comparator<Value>,
): Value = gossipMax(local, comparator.reversed())

/**
 * Self-stabilizing [gossipMin] with a default comparator.
 * Spreads across all (aligned) devices the current minimum [Value] of [local],
 * as computed by first value compared to the second,
 * and then reversed.
 */
fun <ID : Comparable<ID>, Value : Comparable<Value>> Aggregate<ID>.gossipMin(local: Value): Value =
    gossipMin(local) { first, second -> first.compareTo(second) }

/**
 * Returns true if the condition is holding anywhere in the network, false otherwise.
 */
fun <ID : Comparable<ID>> Aggregate<ID>.isHappeningAnywhere(condition: () -> Boolean): Boolean =
    gossipMax(condition()) { first, second -> first.compareTo(second) }

/**
 * A **non-self-stabilizing** gossip function for repeated propagation of a [value] and [aggregation]
 * of state estimates between neighboring devices.
 */
inline fun <ID : Any, reified Value> Aggregate<ID>.nonStabilizingGossip(
    value: Value,
    noinline aggregation: (Value, Value) -> Value,
): Value = share(value) { it.fold(value, aggregation) }

/**
 * A **non-self-stabilizing** function returning `true` if at any point in time a certain [condition] happened.
 *
 * *Note:* due to its non-self-stabilizing nature, if the [condition] does not hold anymore, this function will
 * keep returning `true`.
 * To check whether a condition is still holding, use [isHappeningAnywhere]
 */
fun <ID : Any> Aggregate<ID>.everHappened(condition: () -> Boolean): Boolean =
    nonStabilizingGossip(condition()) { a, b -> a || b }
