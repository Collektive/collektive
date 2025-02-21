/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.stdlib.spreading.gossipMin
import it.unibo.collektive.stdlib.spreading.nonStabilizingGossip
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GossipTest {
    // A stable gossip means that every device of the network has the same value
    private fun <Value> Environment<Value>.gossipIsStable(): Boolean = status().values.distinct().size == 1

    private fun <Value> Environment<Value>.gossipResult(): Value = status().values.distinct().first()

    private fun squareMooreGridWithGossip(
        size: Int,
        max: Boolean = true,
    ) = mooreGrid<Int>(size, size, { _, _ -> Int.MAX_VALUE }) { _, _ ->
        if (max) {
            gossipMax(localId) // gossip the max localID in the network
        } else {
            gossipMin(localId) // gossip the min localID in the network
        }
    }.apply {
        assertEquals(size * size, nodes.size)
        val initial = status().values.distinct()
        assertEquals(1, initial.size)
        check(initial.first() == Int.MAX_VALUE) {
            "Initial status is not `Int.MAX_VALUE`, but it is $initial (${initial::class.simpleName})"
        }
    }

    private fun linearMooreGridWithGossip(
        size: Int,
        max: Boolean = true,
    ) = mooreGrid<Int>(size, 1, { _, _ -> Int.MAX_VALUE }) { _, _ ->
        if (max) {
            gossipMax(localId) // gossip the max localID in the network
        } else {
            gossipMin(localId) // gossip the min localID in the network
        }
    }.apply {
        assertEquals(size, nodes.size)
        val initial = status().values.distinct()
        assertEquals(1, initial.size)
        check(initial.first() == Int.MAX_VALUE) {
            "Initial status is not `Int.MAX_VALUE`, but it is $initial (${initial::class.simpleName})"
        }
    }

    private fun squareMooreGridWithNonSelfStabilizingGossip(size: Int) =
        mooreGrid<Int>(size, size, { _, _ -> Int.MAX_VALUE }) { _, _ ->
            nonStabilizingGossip(localId) { first, second -> if (first >= second) first else second }
        }.apply {
            assertEquals(size * size, nodes.size)
            val initial = status().values.distinct()
            assertEquals(1, initial.size)
            check(initial.first() == Int.MAX_VALUE) {
                "Initial status is not `Int.MAX_VALUE`, but it is $initial (${initial::class.simpleName})"
            }
        }

    @Test
    fun `gossipMax in a square moore grid stabilizes after 2 reverse cycles`() {
        val size = 5
        val environment: Environment<Int> = squareMooreGridWithGossip(size)
        environment.cycleInOrder()
        val firstRound = environment.status()
        assertEquals(size * size, firstRound.values.distinct().size)
        firstRound.forEach { (id, value) -> assertEquals(id, value) }
        assertFalse(environment.gossipIsStable())
        environment.cycleInReverseOrder()
        assertTrue(environment.gossipIsStable())
        assertEquals(24, environment.gossipResult())
    }

    @Test
    fun `gossipMax in the best case stabilizes in one cycle`() {
        val size = 5
        val environment: Environment<Int> = squareMooreGridWithGossip(size)
        environment.cycleInReverseOrder()
        assertTrue(environment.gossipIsStable())
        assertEquals(24, environment.gossipResult())
    }

    @Test
    fun `gossipMax in the worst case stabilizes in the network diameter cycles`() {
        val size = 10
        val environment: Environment<Int> = linearMooreGridWithGossip(size)
        repeat(size - 1) {
            environment.cycleInOrder()
            assertFalse(environment.gossipIsStable())
        }
        environment.cycleInOrder()
        assertTrue(environment.gossipIsStable())
        assertEquals(9, environment.gossipResult())
    }

    @Test
    fun `gossipMin in the best case stabilizes in one cycle`() {
        val size = 5
        val environment: Environment<Int> = squareMooreGridWithGossip(size, max = false)
        environment.cycleInOrder()
        assertTrue(environment.gossipIsStable())
        assertEquals(0, environment.gossipResult())
    }

    @Test
    fun `gossipMin in the worst case stabilizes in the network diameter cycles`() {
        val size = 10
        val environment: Environment<Int> = linearMooreGridWithGossip(size, max = false)
        repeat(size - 1) {
            environment.cycleInReverseOrder()
            assertFalse(environment.gossipIsStable())
        }
        environment.cycleInOrder()
        assertTrue(environment.gossipIsStable())
        assertEquals(0, environment.gossipResult())
    }

    @Test
    fun `gossipMin in a square moore grid stabilizes after 2 reverse cycles`() {
        val size = 5
        val environment: Environment<Int> = squareMooreGridWithGossip(size, max = false)
        environment.cycleInReverseOrder()
        val firstRound = environment.status()
        assertEquals(size * size, firstRound.values.distinct().size)
        firstRound.forEach { (id, value) -> assertEquals(id, value) }
        assertFalse(environment.gossipIsStable())
        environment.cycleInOrder()
        assertTrue(environment.gossipIsStable())
        assertEquals(0, environment.gossipResult())
    }

    @Test
    fun `non-self-stabilizing gossip should not update the best value when it drops from the network`() {
        val size = 5
        val environment: Environment<Int> = squareMooreGridWithNonSelfStabilizingGossip(size)
        repeat(size) {
            environment.cycleInReverseOrder()
        }
        val maxId = environment.nodes.maxBy { it.id }.id
        environment.status().forEach { (_, value) -> assertEquals(maxId, value) }
        repeat(size) {
            environment.nodes.drop(maxId).forEach { n -> n.cycle() }
        }
        environment.status().forEach { (_, value) -> assertEquals(maxId, value) }
    }
}
