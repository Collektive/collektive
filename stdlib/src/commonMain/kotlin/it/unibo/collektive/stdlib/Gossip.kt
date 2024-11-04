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
import it.unibo.collektive.field.Field.Companion.foldWithId

/**
 * Self-stabilizing gossip-max.
 * Spreads across all (aligned) devices the current maximum [Value] of [local],
 * as computed by [selector].
 */
fun <ID : Comparable<ID>, Value> Aggregate<ID>.gossipMax(
    local: Value,
    selector: Comparator<Value>,
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
        val result = gossip.foldWithId(localGossip) { current, id, next ->
            val valid = next.path.asReversed().asSequence().drop(1).none { it == localId || it in neighbors }
            val actualNext = if (valid) next else next.base(id)
            val candidateValue = selector.compare(current.best, actualNext.best)
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
 * A gossip algorithm that computes whether any device is experiencing a certain [condition].
 */
fun <ID : Comparable<ID>> Aggregate<ID>.isHappeningAnywhere(
    condition: () -> Boolean,
): Boolean = gossipMax(condition()) { first, second -> first.compareTo(second) }
