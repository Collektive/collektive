/*
 * Copyright (c) 2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.exchange
import it.unibo.collektive.aggregate.api.exchanging
import it.unibo.collektive.aggregate.api.field.mapValues
import it.unibo.collektive.aggregate.api.mapNeighborhood
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.stdlib.collapse.max
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Covers the operators when `default` and `initial`/`local` are different.
 */
class DefaultAndLocalTest {
    /** All entries, with the default under the `null` id (same convention as [Field.map] and [Field.get]). */
    private fun Field<Int, Int>.toMapWithDefault(): Map<Int?, Int> = all.toMap() + (null to default)

    @Test
    fun `exchange starts from initial but exposes and sends the default`() {
        val result = aggregate(0) {
            val res = exchange(default = 0, initial = 10) { field ->
                assertEquals(mapOf(null to 0, 0 to 10), field.toMapWithDefault())
                field.mapValues { it + 1 }
            }
            assertEquals(mapOf(null to 1, 0 to 11), res.toMapWithDefault())
        }
        // A neighbor with no dedicated message receives the mapped default, not the mapped local
        assertEquals(listOf(1), result.toSend.prepareMessageFor(1).sharedData.values.toList())
    }

    @Test
    fun `exchange evolves the local value while the default stays fixed across rounds`() {
        val env = mooreGrid<Map<Int?, Int>?>(1, 1, null) { _ ->
            var received: Map<Int?, Int>? = null
            exchange(default = 0, initial = 10) { field ->
                received = field.toMapWithDefault()
                field.mapValues { it + 1 }
            }
            received
        }
        val history = List(3) {
            env.cycleInOrder()
            env.status().getValue(0)
        }
        assertEquals((10..12).map { mapOf(null to 0, 0 to it) }, history)
    }

    @Test
    fun `exchanging neighbors read the sender default not its local value`() {
        val env = mooreGrid<Map<Int?, Int>?>(2, 1, null) { _ ->
            exchanging(default = 0, initial = 100) { field ->
                field.mapValues { it + 1 }.yielding { field.toMapWithDefault() }
            }
        }
        env.cycleInOrder()
        assertEquals(
            mapOf(
                0 to mapOf(null to 0, 0 to 100),
                // 0 sent local=101 to itself, default=1 to everybody else
                1 to mapOf(null to 0, 1 to 100, 0 to 1),
            ),
            env.status(),
        )
    }

    @Test
    fun `share starts from initial and exposes the default`() {
        aggregate(0) {
            val res = share(default = 0, initial = 5) { field ->
                assertEquals(mapOf(null to 0, 0 to 5), field.toMapWithDefault())
                field.all.values.max()
            }
            assertEquals(5, res)
        }
    }

    @Test
    fun `sharing starts from initial and exposes the default`() {
        aggregate(0) {
            val res = sharing(default = 0, initial = 5) { field ->
                assertEquals(mapOf(null to 0, 0 to 5), field.toMapWithDefault())
                (field.local.value + 1).yielding { "sent $this" }
            }
            assertEquals("sent 6", res)
        }
    }

    @Test
    fun `neighboring shares local but exposes the default`() {
        val env = mooreGrid<Map<Int?, Int>?>(2, 1, null) { _ ->
            neighboring(default = -1, local = localId + 10).toMapWithDefault()
        }
        env.cycleInOrder()
        assertEquals(
            mapOf(
                0 to mapOf(null to -1, 0 to 10),
                1 to mapOf(null to -1, 1 to 11, 0 to 10),
            ),
            env.status(),
        )
    }

    @Test
    fun `mapNeighborhood with a single lambda computes the default with a null id`() {
        val env = mooreGrid<Map<Int?, Int>?>(2, 1, null) { _ ->
            mapNeighborhood { id: Int? -> id?.times(10) ?: -1 }.toMapWithDefault()
        }
        env.cycleInOrder()
        assertEquals(mapOf(null to -1, 1 to 10, 0 to 0), env.status().getValue(1))
    }

    @Test
    fun `mapNeighborhood with an explicit default uses it for the default entry`() {
        val env = mooreGrid<Map<Int?, Int>?>(2, 1, null) { _ ->
            mapNeighborhood(default = -1) { id: Int -> id * 10 }.toMapWithDefault()
        }
        env.cycleInOrder()
        assertEquals(mapOf(null to -1, 1 to 10, 0 to 0), env.status().getValue(1))
    }
}
