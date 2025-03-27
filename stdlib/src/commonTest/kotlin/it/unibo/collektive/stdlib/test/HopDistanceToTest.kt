/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertEquals

class HopDistanceToTest {
    private fun mooreGridWithHopDistanceToGradient(size: Int) =
        mooreGrid<Int>(size, size, { _, _ -> Int.MAX_VALUE }) { _, _ ->
            hopDistanceTo(localId == 0)
        }.apply {
            assertEquals(size * size, nodes.size)
            val initial = status().values.distinct()
            assertEquals(1, initial.size)
            check(initial.first() == Int.MAX_VALUE) {
                "Initial status is not MAX_VALUE, but it is $initial (${initial::class.simpleName})"
            }
        }

    @Test
    fun `hop distance to should not cause overflow`() {
        val environment = mooreGridWithHopDistanceToGradient(10)
        environment.cycleInOrder()
        assertEquals(environment.status()[0], 0) // The source (localId = 0) has distance 0
    }
}
