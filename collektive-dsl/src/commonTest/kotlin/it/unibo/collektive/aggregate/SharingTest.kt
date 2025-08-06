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
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.collapse.max
import it.unibo.collektive.stdlib.collapse.min
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SharingTest {
    val findMax: (Field<*, Int>) -> Int = { e -> e.includeSelf.values.max() }
    val findMin: (Field<*, Int>) -> Int = { e -> e.includeSelf.values.min() }

    private fun mooreGridWithProgram(
        size: Int,
        program: Aggregate<Int>.(Environment<Pair<Int, Int>>) -> Pair<Int, Int>,
    ) = mooreGrid(size, size, { _, _ -> Int.MIN_VALUE to Int.MAX_VALUE }, program).also {
        assertEquals(size * size, it.nodes.size)
    }

    @Test
    fun `first time sharing`() {
        aggregate(0) {
            val result = share(1, findMax)
            assertEquals(1, result)
        }
    }

    @Test
    fun `share should communicate with aligned devices`() {
        val size = 5
        val environment =
            mooreGridWithProgram(size) { _ ->
                val maxValue = share(localId, findMax)
                val minValue = share(localId, findMin)
                maxValue to minValue
            }
        repeat(size) {
            environment.cycleInOrder()
        }
        assertTrue(environment.status().all { it.value == size * size - 1 to 0 })
    }

    @Test
    fun `share with lambda body should work fine`() {
        val testNetwork = NetworkImplTest(NetworkManager(), 1)

        aggregate(1, testNetwork) {
            val result =
                share(1) {
                    it.includeSelf.values.max()
                }
            assertEquals(1, result)
        }
    }

    @Test
    fun `sharing should return the value passed in the yielding function`() {
        val testNetwork = NetworkImplTest(NetworkManager(), 1)

        aggregate(1, testNetwork) {
            val res =
                sharing(1) {
                    val min = it.includeSelf.values.min()
                    min.yielding { "A string" }
                }
            assertEquals("A string", res)
        }
    }

    @Test
    fun `sharing should work fine even with null as value`() {
        val testNetwork = NetworkImplTest(NetworkManager(), 1)

        aggregate(1, testNetwork) {
            val res =
                sharing(1) {
                    val min = it.includeSelf.values.min()
                    min.yielding { "Hello".takeIf { min > 1 } }
                }
            assertNull(res)
        }
    }
}
