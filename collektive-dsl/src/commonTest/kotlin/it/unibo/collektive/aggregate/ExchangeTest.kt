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
import it.unibo.collektive.aggregate.api.exchanging
import it.unibo.collektive.stdlib.fields.foldValues
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExchangeTest {
    private val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.map { (_, v) -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    private fun mooreGridWithIncreaseOrDouble(size: Int) = mooreGrid<Int>(size, size, { _, _ -> Int.MAX_VALUE }) { _ ->
        exchange(1) { field ->
            field.mapValues { field.foldValues(localId) { acc, value -> acc + value } }
        }.local.value
    }.also {
        assertEquals(size * size, it.nodes.size)
    }

    private fun mooreGridWithConstantFieldResult(size: Int) =
        mooreGrid<Field<Int, Int>>(size, size, { _, _ -> Field.invoke(0, 0, emptyMap()) }) { _ ->
            exchange(1) { field ->
                field.mapToConstant(10)
            }
        }.also {
            assertEquals(size * size, it.nodes.size)
        }

    private fun mooreGridWithDedicatedNeighborValues(size: Int) =
        mooreGrid<Int>(size, size, { _, _ -> Int.MIN_VALUE }) { _ ->
            val res =
                exchange(0) { field ->
                    field.map { (id, _) ->
                        when (id) {
                            0 -> 0
                            else -> 1
                        }
                    }
                }
            res.neighborsValues.sum() + res.local.value
        }.also {
            assertEquals(size * size, it.nodes.size)
        }

    @Test
    fun `exchange on the first round should use the initial value and send the new value according to the function`() {
        val result =
            aggregate(0) {
                val res = exchange(1, increaseOrDouble)
                assertEquals(2, res.local.value)
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        assertEquals(1, messages.size)
        assertContentEquals(listOf(2), messages.values)
    }

    @Test
    fun `exchange should share data with other aligned devices in the network`() {
        val environment = mooreGridWithIncreaseOrDouble(2)
        environment.cycleInOrder()
        val result = environment.status()

        /**
         * Each node id connected to the others in the network supposing to fire the round in order:
         * 0=ϕ(localId=0, localValue=0, neighbors={})
         * 1=ϕ(localId=1, localValue=1, neighbors={0=1})
         * 2=ϕ(localId=2, localValue=3, neighbors={0=3, 1=3})
         * 3=ϕ(localId=3, localValue=7, neighbors={0=7, 1=7, 2=7})
         */
        val expectedResult = mapOf(0 to 0, 1 to 1, 2 to 3, 3 to 7)
        assertEquals(expectedResult, result)
    }

    @Test
    fun `exchange can yield a result but return a different value`() {
        val result =
            aggregate(0) {
                val xcRes =
                    exchanging(1) {
                        val fieldResult = it + 1
                        fieldResult.yielding { fieldResult.mapValues { value -> "return: $value" } }
                    }
                assertEquals(mapOf(0 to "return: 2"), xcRes.toMap())
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        assertEquals(1, messages.size)
        assertContentEquals(listOf(2), messages.values)
    }

    @Test
    fun `exchange can yield a result of nullable values`() {
        val result =
            aggregate(0) {
                val xcRes =
                    exchanging(1) {
                        val fieldResult = it + 1
                        fieldResult.yielding { fieldResult.mapValues { value -> value.takeIf { value > 10 } } }
                    }
                assertEquals(mapOf(0 to null), xcRes.toMap())
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        assertEquals(1, messages.size)
        assertContentEquals(listOf(2), messages.values)
    }

    @Test
    fun `exchange should produce a message with no overrides when producing a constant field`() {
        val size = 2
        val environment = mooreGridWithConstantFieldResult(size)
        // Executes two rounds per device
        environment.cycleInOrder()
        environment.cycleInOrder()
        val result = environment.status()
        assertEquals(1, result.values.distinct().size)
        assertTrue(result.values.all { it is ConstantField })
        assertEquals(listOf(10, 10, 10, 10), result.values.map { it.local.value })
    }

    @Test
    fun `exchange should send dedicated values to neighbors`() {
        val size = 3
        val environment = mooreGridWithDedicatedNeighborValues(size)
        // Executes two rounds per device
        for (i in 0 until size * size) {
            environment.cycleInOrder()
        }
        val result = environment.status()
        // Program rationale: if you have the device with ID=0 in the neighbor, it counts as 0, otherwise 1
        val expectedLocalValues = mapOf(0 to 3, 1 to 5, 2 to 4, 3 to 5, 4 to 8, 5 to 6, 6 to 4, 7 to 6, 8 to 4)
        assertEquals(expectedLocalValues, result)
    }
}
