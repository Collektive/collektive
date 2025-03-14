/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.aggregate.api.Aggregate
import kotlin.random.Random

/**
 * An environment with a set of [nodes] mapped into [positions],
 * which are considered connected if the [areConnected] function returns true for them.
 */
open class Environment<R>(val areConnected: (Environment<R>, Node<R>, Node<R>) -> Boolean = { _, _, _ -> true }) {
    private val positions = mutableMapOf<Node<R>, Position>()
    private val randomGenerator = Random(0)
    private var nextId = 0

    /**
     * The set of nodes in the environment.
     */
    val nodes: Set<Node<R>> get() = positions.keys

    /**
     * The map of nodes and their positions in the environment.
     */
    val nodesAndPositions: Map<Node<R>, Position> get() = positions

    /**
     * Adds a node to the environment at the given [position] with the given [initial] value and [program].
     */
    fun addNode(position: Position, initial: R, program: Aggregate<Int>.(Environment<R>, Int) -> R) {
        positions[Node(this, nextId++, initial, program)] = position
    }

    /**
     * Retrieves the position of a [node].
     */
    fun positionOf(node: Node<R>) = positions.getValue(node)

    /**
     * Retrieves the position of a node with the given [id].
     */
    fun positionOf(id: Int) = positions.firstNotNullOf { (node, position) -> position.takeIf { node.id == id } }

    /**
     * Retrieves the neighboring nodes of the provided [node].
     */
    fun neighborsOf(node: Node<R>): List<Node<R>> = positions.keys.filter { it != node && areConnected(this, node, it) }

    override fun toString() = "Environment(nodes=$nodes)"

    /**
     * Runs a Collektive cycles for all the nodes in the environment,
     * from the node with the lowest id to the one with the highest, in order.
     */
    fun cycleInOrder() = orderAndCycle { sortedBy { it.id } }

    /**
     * Runs a Collektive cycles for all the nodes in the environment,
     * from the node with the lowest id to the one with the highest, in order.
     */
    fun cycleInReverseOrder() = orderAndCycle { sortedWith(compareBy<Node<R>> { it.id }.reversed()) }

    /**
     * Runs a Collektive cycles for all the nodes in the environment, in random (but repeatable) order.
     */
    fun cycleInRandomOrder() = orderAndCycle { shuffled(randomGenerator) }

    private fun orderAndCycle(sorted: Set<Node<R>>.() -> List<Node<R>>) {
        nodes.sorted().forEach { it.cycle() }
    }

    /**
     * Returns the current status of the environment as a map of node IDs to their values.
     */
    fun status() = nodes.associate { it.id to it.value }
}
