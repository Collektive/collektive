/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.all
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NeighboringTest {
    private fun mooreGrid(size: Int, program: Aggregate<Int>.(Environment<Field<Int, Int>>, Int) -> Field<Int, Int>) =
        mooreGrid(size, size, { _, _ -> Field(Int.MAX_VALUE, 0) }, program).also {
            assertEquals(size * size, it.nodes.size)
        }

    @Test
    fun `neighboring must produce a field with the local value when no neighbours are present`() {
        aggregate(0) {
            val field = neighboring(1)
            assertContains(field.toMap().values, 1)
            assertEquals(1, field.localValue)
        }
    }

    @Test
    fun `optimized neighboring must produce a field with the local value when no neighbours are present`() {
        aggregate(0) {
            val field = neighboring(1)
            assertContains(field.toMap().values, 1)
            assertEquals(1, field.localValue)
        }
    }

    private fun envWithNeihboring1(size: Int) = mooreGrid(size) { _, _ -> neighboring(1) }

    @Test
    fun `neighboring should build a field containing the values of the aligned neighbors`() {
        val size = 5
        val environment = envWithNeihboring1(size)
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (_, field) ->
            assertEquals(1, field.localValue)
            assertTrue(field.all { it == 1 })
        }
    }

    @Test
    fun `optimized neighboring should build a field containing the values of the aligned neighbors`() {
        val size = 10
        val environment = envWithNeihboring1(size)
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (_, field) ->
            assertEquals(1, field.localValue)
            assertTrue(field.all { it == 1 })
        }
    }

    private fun envWithTwoNeighboringOpsInBranch(size: Int, condition: (Int) -> Boolean) = mooreGrid(size) { _, id ->
        fun kingBehavior() = neighboring(1)

        fun queenBehavior() = neighboring(2)
        if (condition(id)) kingBehavior() else queenBehavior()
    }

    @Test
    fun `only aligned devices should communicate each other`() {
        val size = 2
        val condition: (Int) -> Boolean = { it % 2 == 0 }
        val environment = envWithTwoNeighboringOpsInBranch(size, condition)
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (id, field) ->
            if (condition(id)) {
                assertEquals(1, field.localValue)
                assertTrue(field.all { it == 1 })
            } else {
                assertEquals(2, field.localValue)
                assertTrue(field.all { it == 2 })
            }
        }
    }

    @Test
    fun `only aligned devices should communicate each other with optimized neighboring`() {
        val size = 4
        val condition: (Int) -> Boolean = { it % 2 == 0 }
        val environment = envWithTwoNeighboringOpsInBranch(size, condition)
        repeat(size - 1) {
            environment.cycleInOrder()
        }
        environment.status().forEach { (id, field) ->
            val expectedValue = if (condition(id)) 1 else 2
            assertEquals(expectedValue, field.localValue)
            assertTrue(field.all { it == expectedValue })
        }
    }
}
