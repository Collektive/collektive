/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.collapse.max
import it.unibo.collektive.stdlib.collapse.min
import kotlin.test.Test
import kotlin.test.assertEquals

class FieldManipulationTest {
    private val doubleOf: (Int) -> Int = { it * 2 }

    // ids
    private val id0 = 0
    private val id1 = 1

    @Test
    fun `get the min value including self`() {
        aggregate(id0) {
            val sharedField = neighboring(doubleOf(3))
            assertEquals(6, sharedField.includeSelf.values.min)
        }

        aggregate(id1) {
            val sharedField = neighboring(doubleOf(2))
            assertEquals(4, sharedField.includeSelf.values.min)
        }
    }

    @Test
    fun `get the max value including self issue 767`() {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val sharedField = neighboring(doubleOf(3))
            assertEquals(6, sharedField.includeSelf.values.max)
        }

        aggregate(id1, network1) {
            val sharedField = neighboring(doubleOf(4))
            assertEquals(8, sharedField.includeSelf.values.max)
        }
    }

    @Test
    fun `get min value non including self`() {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val minValue = neighboring(doubleOf(3)).excludeSelf.values.min
            assertEquals(Int.MAX_VALUE, minValue)
        }

        aggregate(id1, network1) {
            val minValue = neighboring(doubleOf(2)).excludeSelf.values.min
            assertEquals(6, minValue)
        }
    }

    @Test
    fun `get max value non including self`() {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val maxValue = neighboring(doubleOf(3)).excludeSelf.values.max
            assertEquals(Int.MIN_VALUE, maxValue)
        }

        aggregate(id1, network1) {
            val maxValue = neighboring(doubleOf(2)).excludeSelf.values.max
            assertEquals(6, maxValue)
        }
    }
}
