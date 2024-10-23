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
 * The [value] exchanged in the gossip algorithm.
 * It contains the [value] itself and the [path] of nodes through which it has passed.
 */
private data class GossipValue<ID : Comparable<ID>, Type>(val value: Type, val path: List<ID>)

/**
 * Gossip algorithm implementation.
 * Each node starts with an [initial] value, shares it with its neighbors,
 * and updates its value based on the best value received according to the [selector].
 */
fun <ID : Comparable<ID>, Type> Aggregate<ID>.gossip(
    initial: Type,
    selector: (Type, Type) -> Boolean,
): Type {
    val local = GossipValue(initial, emptyList<ID>())
    return share(local) { gossip ->
        gossip.fold(local) { current, next ->
            val selected = when {
                selector(current.value, next.value) || localId in next.path -> current
                else -> next
            }
            selected.copy(path = selected.path + localId)
        }
    }.value
}

/**
 * A gossip algorithm that computes whether any device is experiencing a certain [condition].
 */
fun <ID : Comparable<ID>> Aggregate<ID>.isHappeningAnywhere(
    condition: () -> Boolean,
): Boolean = gossip(condition()) { _, _ -> condition() }
