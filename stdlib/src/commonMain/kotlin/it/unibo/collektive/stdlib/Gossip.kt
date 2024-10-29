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
import it.unibo.collektive.field.Field.Companion.fold

/**
 * Gossip algorithm implementation.
 * Each node starts with an [initial] value, shares it with its neighbors,
 * and updates its value based on the best value received according to the [selector].
 */
fun <ID : Comparable<ID>, Value> Aggregate<ID>.gossip(
    initial: Value,
    selector: Comparator<Value>,
): Value {
    val local = GossipValue<ID, Value>(initial, initial)
    return share(local) { gossip ->
        val result = gossip.fold(local) { current, next ->
            val actualNext = if (localId in next.path) next.base() else next
            val candidateValue = selector.compare(current.best, actualNext.best)
            when {
                candidateValue > 0 -> current
                candidateValue == 0 -> listOf(current, next).minBy { it.path.size }
                else -> actualNext
            }
        }
        GossipValue(result.best, initial, result.path + localId)
    }.best
}

/**
 * A gossip algorithm that computes whether any device is experiencing a certain [condition].
 */
fun <ID : Comparable<ID>> Aggregate<ID>.isHappeningGossip(
    condition: () -> Boolean,
): Boolean = gossip(condition()) { first, second -> first.compareTo(second) }

/**
 * The best value exchanged in the gossip algorithm.
 * It contains the [best] value evaluated yet,
 * the [local] value of the node and the [path] of nodes through which it has passed.
 */
private data class GossipValue<ID : Comparable<ID>, Value>(
    val best: Value,
    val local: Value,
    val path: List<ID> = emptyList(),
) {
    /**
     * Returns the base node itself, with its own value set to best and local.
     */
    fun base() = GossipValue(local, local, listOf(path.last()))
}
