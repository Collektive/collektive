import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.distanceTo
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.math.abs
import kotlin.math.sqrt

/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti, and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

class TestDistanceTo : StringSpec({
    fun Environment<Double>.gradientIsStable(isMoore: Boolean): Boolean = status().all { (id, value) ->
        val x = id % 10
        val y = id / 10
        val steps = x + y
        val expected = when {
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
    fun mooreGridWithGradient() =
        mooreGrid<Double>(10, 10, { _, _ -> Double.NaN }) { environment ->
            val localPosition = environment.positionOf(localId)
            distanceTo(localId == 0) { neighboring(localPosition).map { it.distanceTo(localPosition) } }
        }.apply {
            nodes.size shouldBe 100
            val initial = status().values.distinct()
            initial.size shouldBe 1
            check(initial.first().isNaN()) {
                "Initial status is not NaN, but it is $initial (${initial::class.simpleName})"
            }
        }
    "distanceTo in the luckiest case stabilizes in one cycle" {
        val environment: Environment<Double> = mooreGridWithGradient()
        environment.cycleInOrder()
        environment.gradientIsStable(isMoore = true) shouldBe true
    }
    "distanceTo requires at most the longest path length cycles to stabilize" {
        val environment: Environment<Double> = mooreGridWithGradient()
        environment.cycleInReverseOrder()
        val firstRound = environment.status()
        firstRound[0] shouldBe 0.0
        firstRound.forEach { (id, value) ->
            when (id) {
                0 -> value shouldBe 0.0
                else -> value shouldBe Double.POSITIVE_INFINITY
            }
        }
        repeat(times = 9) {
            environment.gradientIsStable(isMoore = true) shouldBe false
            environment.cycleInReverseOrder()
        }
        environment.gradientIsStable(isMoore = true) shouldBe true
    }
})
