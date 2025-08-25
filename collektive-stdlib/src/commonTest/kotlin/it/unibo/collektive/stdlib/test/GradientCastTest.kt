/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.stdlib.test

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.spreading.bellmanFordGradientCast
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.stdlib.util.PathValue
import it.unibo.collektive.stdlib.util.euclideanDistance3D
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.EnvironmentWithMeshNetwork
import it.unibo.collektive.testing.Position
import it.unibo.collektive.testing.SerializingMailbox
import it.unibo.collektive.testing.vonNeumannGrid
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.nextDown
import kotlin.math.nextUp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class GradientCastTest {

    @Test
    fun `GradientPath is serializable`() {
        val network = SerializingMailbox()
        aggregate(0, network) {
            neighboring(PathValue(0, 0, emptyList<String>()))
            neighboring(PathValue(0, 0, listOf(1, 2, 3)))
        }
        assertNotNull(network.serializeToString(1))
    }

    /**
     * @param program an aggregate program that can access the environment and the y coordinate of the node
     */
    private fun threeNodeEnvironment(program: Aggregate<Int>.(env: Environment<Double>, y: Double) -> Double) =
        EnvironmentWithMeshNetwork<Double>(
            connectDistance = 1.1,
            defaultRetainTime = 2,
        ).apply {
            val left = 0.0.nextDown()
            val right = 0.0.nextUp()
            for ((x, y) in listOf(0.0 to 0.0, 1.0 to left, 1.0 to right)) {
                addNode(Position(x, y, 0.0), Double.NaN) {
                    program(this@apply, y)
                }
            }
        }

    private fun Environment<Double>.stabilizeAndDropSource() {
        assertTrue(nodes.all { it.value.isNaN() })
        repeat(2) { cycleInRandomOrder() }
        assertTrue(nodes.all { it.value == 0.0 })
        removeNode(0)
    }

    @Test
    fun `gradientCast does not suffer from the raising value problem`() {
        threeNodeEnvironment { env, y ->
            gradientCast(
                source = localId == 0,
                local = y,
                metric = euclideanDistance3D(env.positionOf(localId).toTriple()),
            )
        }.apply {
            stabilizeAndDropSource()
            repeat(4) { cycleInRandomOrder() }
            val left = 0.0.nextDown()
            val right = 0.0.nextUp()
            assertEquals(left, nodes.single { it.id == 1 }.value)
            assertEquals(right, nodes.single { it.id == 2 }.value)
        }
    }

    @Test
    fun `Bellman-Ford does suffer from the raising value problem`() {
        threeNodeEnvironment { env, y ->
            bellmanFordGradientCast(
                source = localId == 0,
                local = y,
                metric = euclideanDistance3D(env.positionOf(localId).toTriple()),
            )
        }.apply {
            stabilizeAndDropSource()
            repeat(100) { cycleInRandomOrder() }
            assertTrue(nodes.all { it.value == 0.0 })
        }
    }

    fun testManhattanDistance(program: Aggregate<Int>.(Environment<Int>) -> Int) {
        vonNeumannGrid(10, 10, Int.MIN_VALUE, program).apply {
            cycleInOrder()
            nodes.forEach { node ->
                val (x, y) = positionOf(node.id)
                val manhattanDistance = x + y
                assertEquals(manhattanDistance.toInt(), node.value, "Node $node has not the expected distance")
            }
        }
    }

    @Test
    fun `the hop count in a Von Neumann grid is the Manhattan distance`() {
        testManhattanDistance {
            hopDistanceTo(localId == 0)
        }
    }

    @Test
    fun `gradientCast supports multiple sources`() {
        vonNeumannGrid(10, 10, Int.MAX_VALUE) {
            hopDistanceTo(localId == 0 || localId == 99)
        }.apply {
            repeat(10) {
                // In a diameter cycles all information should have been propagated
                cycleInOrder()
            }
            nodes.forEach { node ->
                val (x, y) = positionOf(node.id)
                val manhattanDistance = min(x + y, abs(9 - x) + abs(9 - y))
                val distance = node.value
                assertEquals(manhattanDistance.toInt(), distance, "Node $node has not the expected distance")
            }
        }
    }
}
