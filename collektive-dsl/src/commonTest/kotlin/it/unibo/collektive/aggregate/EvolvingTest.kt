/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.exchange
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import it.unibo.collektive.testing.Round.roundFor
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertNull

class EvolvingTest {
    private val doubleValue: (Int) -> Int = { it * 2 }
    private val initialValue = 1

    @Test
    fun `evolve should use execute the given function against the initial value on the first round`() {
        aggregate(0) {
            val result = evolve(initialValue, doubleValue)
            assertEquals(2, result)
        }
    }

    @Test
    fun `evolve when executed multiple times should execute the given function against the most recent state`() {
        val finalResult =
            roundFor(steps = 2, deviceId = 0) {
                evolve(initialValue, doubleValue)
            }
        assertEquals(4, finalResult.newState.values.first())
    }

    @Test
    fun `evolve should accept a lambda function as a function body describing the evolution logic`() {
        aggregate(0) {
            val result = evolve(initialValue) { it * 10 }
            assertEquals(10, result)
        }
    }

    @Test
    fun `evolving should evolve the value but should yield a different passed in the yielding function`() {
        val producedResult =
            roundFor(steps = 10, deviceId = 0) {
                val evolvingResult =
                    evolving(0) {
                        (it + 1).yielding { "A string" }
                    }
                assertEquals("A string", evolvingResult)
                evolvingResult
            }
        assertEquals(1, producedResult.newState.values.size)
        assertContains(producedResult.newState.values, 10)
    }

    @Test
    fun `evolving should properly manage also null values when yielding a different value`() {
        aggregate(0) {
            val result =
                evolving(initialValue) {
                    val mult = it * 2
                    mult.yielding { "Hello".takeIf { mult < 1 } }
                }
            assertNull(result)
        }
    }

    @Test
    fun `evolve should raise an exception when a field is returned`() {
        assertFails {
            aggregate(0) {
                exchange(0) { field ->
                    evolve(field) { it + 1 }
                }
            }
        }
    }

    @Test
    fun `evolving should raise an exception when a field is returned`() {
        assertFails {
            aggregate(0) {
                exchange(0) { field ->
                    evolving(field) {
                        val result = it + 1
                        result.yielding { field.map { it + 2 } }
                    }
                }
            }
        }
    }
}
