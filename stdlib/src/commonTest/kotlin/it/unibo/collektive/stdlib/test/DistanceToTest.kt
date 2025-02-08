/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.stdlib.test

import it.unibo.collektive.aggregate.api.Aggregate.Companion.neighboring
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DistanceToTest {
    private fun Environment<Double>.gradientIsStable(isMoore: Boolean): Boolean =
        status().all { (id, value) ->
            val x = id % 10
            val y = id / 10
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
            distanceTo(localId == 0) { neighboring(localPosition).map { it.distanceTo(localPosition) } }
        }.apply {
            assertEquals(100, nodes.size)
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
        assertTrue(environment.gradientIsStable(isMoore = true))
    }

    @Test
    fun `distanceTo requires at most the longest path length cycles to stabilize`() {
        val size = 10
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
            assertFalse(environment.gradientIsStable(isMoore = true))
            environment.cycleInReverseOrder()
        }

        assertTrue(environment.gradientIsStable(isMoore = true))
    }
}
