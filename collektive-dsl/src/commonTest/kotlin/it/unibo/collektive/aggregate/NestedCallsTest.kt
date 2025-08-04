/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.collapse.min
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import kotlin.Double.Companion.POSITIVE_INFINITY
import kotlin.test.Test
import kotlin.test.assertEquals

class NestedCallsTest {
    fun Aggregate<Int>.foo(id: Int) = neighboring(id.toDouble())

    fun Aggregate<Int>.bar(): Double = share(POSITIVE_INFINITY) { dist ->
        when (localId) {
            0 -> 0.0
            else -> (foo(localId) + dist).excludeSelf.values().min() ?: POSITIVE_INFINITY
        }
    }

    @Test
    fun `regression test for the issue 207`() {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, 0)
        val network1 = NetworkImplTest(networkManager, 1)

        val collektiveDevice0 = Collektive(0, network0) { bar() }
        assertEquals(collektiveDevice0.cycle(), 0.0)

        val collektiveDevice1 = Collektive(1, network1) { bar() }
        assertEquals(Double.POSITIVE_INFINITY, collektiveDevice1.cycle())
    }
}
