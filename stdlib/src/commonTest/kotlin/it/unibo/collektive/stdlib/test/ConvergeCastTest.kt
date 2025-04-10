/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.stdlib.test

import it.unibo.collektive.stdlib.accumulation.convergeCast
import it.unibo.collektive.stdlib.accumulation.countDevices
import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.util.Point3D
import it.unibo.collektive.stdlib.util.euclideanDistance3D
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.Position
import it.unibo.collektive.testing.mooreGrid
import kotlin.Int.Companion.MIN_VALUE
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class ConvergeCastTest {
    @Test
    fun `convergeCast can count devices`() {
        val environment = mooreGrid<Int>(10, 10, { _, _ -> MIN_VALUE }) {
            countDevices(localId == 0)
        }
        environment.cycleInOrder()
        assertTrue(environment.nodes.all { it.value == 1 })
        var valueAtSink = 1
        repeat(9) {
            environment.cycleInOrder()
            val currentValueAtSink = environment[0].value
            assertTrue(currentValueAtSink > valueAtSink)
            valueAtSink = currentValueAtSink
        }
        assertEquals(environment.nodes.size, valueAtSink)
    }

    @Test
    fun `convergeCast with a custom metric can accumulate on lists`() {
        Environment<List<Int>> { env, n1, n2 -> env.positionOf(n1).distanceTo(env.positionOf(n2)) <= 2.5 }.apply {
            val nodeCount = 100
            val bounds = 0.0..8.0
            val random = Random(0)
            fun randomCoordinate() = random.nextDouble(bounds.start, bounds.endInclusive)
            fun randomPosition() = Position(randomCoordinate(), randomCoordinate(), randomCoordinate())
            fun addNode() = addNode(randomPosition(), emptyList<Int>()) {
                val metric = euclideanDistance3D { Point3D(positionOf(it).toTriple()) }
                val distance = distanceTo(localId == 0, metric = metric)
                convergeCast(
                    local = listOf(localId),
                    potential = distance,
                    accumulateData = { x, y -> x + y },
                )
            }
            repeat(nodeCount) { addNode() }
            repeat(20) { cycleInOrder() }
            val allNodes: List<Int> = nodes.map { it.id }.sorted()
            assertEquals(allNodes, get(0).value.sorted())
            nodes.asSequence().drop(1).forEach {
                val partial: List<Int> = it.value.sorted()
                assertNotEquals(allNodes, partial)
                assertTrue(allNodes.containsAll(partial))
            }
        }
    }
}
