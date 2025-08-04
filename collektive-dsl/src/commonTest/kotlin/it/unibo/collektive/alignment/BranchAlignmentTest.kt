/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.alignment

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.exchange
import it.unibo.collektive.aggregate.api.mapNeighborhood
import it.unibo.collektive.aggregate.api.neighborhood
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.collapse.minValue
import it.unibo.collektive.stdlib.ints.FieldedInts.minus
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BranchAlignmentTest {
    @Test
    fun `Branch alignment should work in nested functions`() {
        val result =
            aggregate(0) {
                val condition = true

                fun test() {
                    neighboring("test")
                }

                fun test2() {
                    test()
                }
                if (condition) {
                    test2()
                }
            }
        val messageFor1 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor1.size) // 1 path of alignment
        assertEquals(listOf("test"), messageFor1.values.toList())
    }

    @Test
    fun `Branch alignment should not occur in non aggregate context`() {
        val result =
            aggregate(0) {
                val condition = true

                fun test(): String = "hello"

                fun test2() {
                    test()
                }
                if (condition) {
                    test2()
                }
            }
        assertEquals(
            0,
            result.toSend
                .prepareMessageFor(0)
                .sharedData.size,
        ) // 0 paths of alignment
    }

    @Test
    fun `A field should be projected when used in a body of a branch condition issue 171`() {
        val nm = NetworkManager()
        (0..2)
            .map { NetworkImplTest(nm, it) to it }
            .map { (net, id) ->
                aggregate(id, net) {
                    val outerField = neighboring(0)
                    assertEquals(id, outerField.neighborsCount)
                    if (id % 2 == 0) {
                        assertEquals(id / 2, neighboring(1).neighborsCount)
                        neighboring(1) + outerField
                        assertEquals(neighborhood().neighborsCount, outerField.neighborsCount)
                        1
                    } else {
                        assertEquals((id - 1) / 2, neighboring(1).neighborsCount)
                        neighboring(1) - outerField
                        assertEquals(neighborhood().neighborsCount, outerField.neighborsCount)
                        outerField.minValue(Int.MAX_VALUE)
                    }
                }
            }
    }

    private fun exchangeWithThreeDevices(body: Aggregate<Int>.(Field<Int, Int>) -> Field<Int, Int>) {
        val nm = NetworkManager()
        (0..2)
            .map { NetworkImplTest(nm, it) to it }
            .map { (net, id) ->
                aggregate(id, net) {
                    exchange(0) { body(it) }
                }
            }
    }

    @Test
    fun `A field should be projected also when the field is referenced as lambda parameter issue 171`() {
        exchangeWithThreeDevices {
            if (localId % 2 == 0) {
                assertEquals(neighborhood().neighborsCount, it.neighborsCount)
            } else {
                assertEquals(neighborhood().neighborsCount, it.neighborsCount)
            }
            mapNeighborhood { 1 }
        }
    }

    private fun manuallyAlignedExchangeWithThreeDevices(pivot: (Int) -> Any?) = exchangeWithThreeDevices { field ->
        val neighborsBeforeRestriction = field.neighborsCount
        assertTrue(localId == 0 || neighborsBeforeRestriction > 0)
        // Map the pivots to the number of neighbors that are pivoting on the same value
        val expectations = mapNeighborhood(pivot).neighborsValues.groupBy { it }.mapValues { it.value.size }
        alignedOn(pivot(localId)) {
            val neighborsAfterRestriction = neighborhood().neighborsCount
            val restrictedField = field.neighborsCount
            assertEquals(neighborsAfterRestriction, restrictedField)
            assertEquals(expectations[pivot(localId)] ?: 0, restrictedField)
        }
        field
    }

    @Test
    fun `A field should be projected whenever there is an alignment operation not just on branches issue 171`() {
        manuallyAlignedExchangeWithThreeDevices { it % 2 == 0 }
    }

    @Test
    fun `A field should be projected whenever there is an alignment regardless of the type not just bools issue 171`() {
        manuallyAlignedExchangeWithThreeDevices { it % 2 }
    }

    @Test
    fun `A field should be projected when it is a non-direct receiver issue 171`() {
        exchangeWithThreeDevices {
            with(it) {
                with(local.id % 2 == 0) {
                    if (this) {
                        assertEquals(neighborhood().neighborsCount, neighborsCount)
                        alignedMapValues(neighboring(1)) { a, b -> a + b }.local
                    } else {
                        assertEquals(neighborhood().neighborsCount, it.neighborsCount)
                        alignedMapValues(neighboring(1)) { a, b -> a - b }.local
                    }
                }
            }
            mapNeighborhood { 1 }
        }
    }
}
