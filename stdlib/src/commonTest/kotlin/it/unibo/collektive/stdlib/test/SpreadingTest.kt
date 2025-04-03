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
import it.unibo.collektive.stdlib.spreading.GradientPath
import it.unibo.collektive.stdlib.spreading.bellmanFordGradientCast
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.spreading.gradientCast
import it.unibo.collektive.stdlib.util.euclideanDistance3D
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.EnvironmentWithMeshNetwork
import it.unibo.collektive.testing.Position
import it.unibo.collektive.testing.SerializingMailbox
import it.unibo.collektive.testing.mooreGrid
import kotlin.math.abs
import kotlin.math.nextDown
import kotlin.math.nextUp
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SpreadingTest {
    private fun Environment<Double>.gradientIsStable(isMoore: Boolean, size: Int): Boolean =
        status().all { (id, value) ->
            val x = id % size
            val y = id / size
            val steps = x + y
            val expected =
                when {
                    isMoore -> {
                        val manhattanSteps = abs(x - y)
                        check((steps - manhattanSteps) % 2 == 0)
                        val diagonalSteps = (steps - manhattanSteps) / 2
                        sqrt(2.0) * diagonalSteps + manhattanSteps
                    }

                    else -> steps
                }
            abs(value - expected.toFloat()) < 1e-6
        }

    private fun mooreGridWithGradient(size: Int) =
        mooreGrid<Double>(size, size, { _, _ -> Double.NaN }) { environment ->
            val localPosition = environment.positionOf(localId)
            distanceTo(localId == 0) { euclideanDistance3D(localPosition.toTriple()) }
        }.apply {
            assertEquals(size * size, nodes.size)
            val initial = status().values.distinct()
            assertEquals(1, initial.size)
            check(initial.first().isNaN()) {
                "Initial status is not NaN, but it is $initial (${initial::class.simpleName})"
            }
        }

    @Test
    fun `distanceTo in the luckiest case stabilizes in one cycle`() {
        val environment: Environment<Double> = mooreGridWithGradient(10)
        environment.cycleInOrder()
        assertTrue(environment.gradientIsStable(isMoore = true, 10))
    }

    @Test
    fun `distanceTo requires at most the longest path length cycles to stabilize`() {
        val size = 5
        val environment: Environment<Double> = mooreGridWithGradient(size)
        environment.cycleInReverseOrder()
        val firstRound = environment.status()
        assertEquals(0.0, firstRound[0])
        firstRound.forEach { (id, value) ->
            when (id) {
                0 -> assertEquals(0.0, value)
                else -> assertEquals(Double.POSITIVE_INFINITY, value)
            }
        }
        repeat(size - 1) {
            assertFalse(environment.gradientIsStable(isMoore = true, size))
            environment.cycleInReverseOrder()
        }
        assertTrue(environment.gradientIsStable(isMoore = true, size))
    }

    @Test
    fun `GradientPath is serializable`() {
        val network = SerializingMailbox()
        aggregate(0, network) {
            neighboring(GradientPath(0, 0, 0, 0))
            neighboring(GradientPath(0, 0, 0, listOf(1, 2, 3)))
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
            ) { euclideanDistance3D(env.positionOf(localId).toTriple()) }
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
            ) { euclideanDistance3D(env.positionOf(localId).toTriple()) }
        }.apply {
            stabilizeAndDropSource()
            repeat(100) { cycleInRandomOrder() }
            assertTrue(nodes.all { it.value == 0.0 })
        }
    }
}
