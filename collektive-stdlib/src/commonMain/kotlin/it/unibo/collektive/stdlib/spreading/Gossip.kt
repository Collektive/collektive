/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:Suppress("MatchingDeclarationName")
/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.ids
import it.unibo.collektive.aggregate.values
import it.unibo.collektive.stdlib.collapse.fold
import it.unibo.collektive.stdlib.collapse.reduce
import it.unibo.collektive.stdlib.util.Reducer
import kotlinx.serialization.Serializable

/**
 * The best value exchanged in the gossip algorithm.
 * It contains the [best] value evaluated yet,
 * the [local] value of the node, and the [path] of nodes through which it has passed.
 */
@Serializable
data class GossipValue<ID : Comparable<ID>, Value>(
    val best: Value,
    val local: Value,
    val path: List<ID> = emptyList(),
) {
    /**
     * TODO.
     */
    fun base(id: ID) = GossipValue(local, local, listOf(id))

    /**
     * TODO.
     */
    fun addHop(local: Value, id: ID) = GossipValue(best, local, path + id)
}

/**
 * Self-stabilizing gossip-max.
 * Spreads across all (aligned) devices the current maximum [Value] of [local],
 * as computed by [comparator].
 */
inline fun <reified ID : Comparable<ID>, reified Value> Aggregate<ID>.gossipMax(
    local: Value,
    comparator: Comparator<Value>,
): Value {
    val localGossip = GossipValue<ID, Value>(best = local, local = local)
    val maxGossip = share(localGossip) { gossip ->
        val neighbors = gossip.neighbors
        val result = gossip.excludeSelf.fold(localGossip) { current, (id, next) ->
            val valid = next.path.asReversed().asSequence()
                .drop(1)
                .none { it == localId || it in neighbors.ids.set }
            val actualNext = if (valid) next else next.base(id)
            val candidateValue = comparator.compare(current.best, actualNext.best)
            when {
                candidateValue > 0 -> current
                candidateValue == 0 -> listOf(current, next).minBy { it.path.size }
                else -> actualNext
            }
        }
        result.addHop(local, localId)
    }
    return maxGossip.best
}

/**
 * Self-stabilizing [gossipMax] with a default comparator.
 * Spreads across all (aligned) devices the current maximum [Value] of [local],
 * as computed by first value compared to the second.
 */
inline fun <reified ID : Comparable<ID>, reified Value : Comparable<Value>> Aggregate<ID>.gossipMax(
    local: Value,
): Value = gossipMax(local) { first, second -> first.compareTo(second) }

/**
 * Self-stabilizing gossip-min.
 * Spreads across all (aligned) devices the current minimum [Value] of [local],
 * as computed by [comparator].
 */
inline fun <reified ID : Comparable<ID>, reified Value> Aggregate<ID>.gossipMin(
    local: Value,
    comparator: Comparator<Value>,
): Value = gossipMax(local, comparator.reversed())

/**
 * Self-stabilizing [gossipMin] with a default comparator.
 * Spreads across all (aligned) devices the current minimum [Value] of [local],
 * as computed by first value compared to the second,
 * and then reversed.
 */
inline fun <reified ID : Comparable<ID>, reified Value : Comparable<Value>> Aggregate<ID>.gossipMin(
    local: Value,
): Value = gossipMin(local) { first, second -> first.compareTo(second) }

/**
 * Returns true if the condition is holding anywhere in the network, false otherwise.
 */
inline fun <reified ID : Comparable<ID>> Aggregate<ID>.isHappeningAnywhere(condition: () -> Boolean): Boolean =
    gossipMax(condition()) { first, second -> first.compareTo(second) }

/**
 * A **non-self-stabilizing** gossip function for repeated propagation of a [value] and [reducer]
 * of state estimates between neighboring devices.
 */
inline fun <ID : Any, reified Value> Aggregate<ID>.nonStabilizingGossip(
    value: Value,
    noinline reducer: Reducer<Value>,
): Value = share(value) { it.includeSelf.values.reduce(reducer) }

/**
 * A **non-self-stabilizing** function returning `true` if at any point in time a certain [condition] happened.
 *
 * *Note:* due to its non-self-stabilizing nature, if the [condition] does not hold anymore, this function will
 * keep returning `true`.
 * To check whether a condition is still holding, use [isHappeningAnywhere]
 */
fun <ID : Any> Aggregate<ID>.everHappened(condition: () -> Boolean): Boolean =
    nonStabilizingGossip(condition()) { a, b -> a || b }
