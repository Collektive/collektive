/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.stdlib.test

import it.unibo.collektive.stdlib.accumulation.countDevices
import it.unibo.collektive.testing.mooreGrid
import kotlin.Int.Companion.MIN_VALUE
import kotlin.test.Test
import kotlin.test.assertEquals
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
}
