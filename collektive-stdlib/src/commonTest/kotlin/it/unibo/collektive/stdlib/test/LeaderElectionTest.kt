/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import it.unibo.collektive.stdlib.consensus.boundedElection
import it.unibo.collektive.stdlib.consensus.globalElection
import it.unibo.collektive.testing.forEachPerformanceProfile
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class LeaderElectionTest {

    @Test
    fun `performing a global election selects a single leader`() = forEachPerformanceProfile { profile ->
        mooreGrid(10, 10, Int.MIN_VALUE, performanceProfile = profile) { globalElection() }.apply {
            repeat(10) { cycleInOrder() }
            val leaders = nodes.map { it.value }.distinct()
            assertEquals(1, leaders.size)
            assertNotNull(get(leaders.single()))
        }
    }

    @Test
    fun `performing a bounded election selects multiple leaders`() = forEachPerformanceProfile { profile ->
        mooreGrid(10, 10, Int.MIN_VALUE, performanceProfile = profile) { boundedElection(-localId, 5) }.apply {
            repeat(10) { cycleInOrder() }
            val leaders = nodes.map { it.value }.distinct()
            assertEquals(4, leaders.size)
            assertTrue(0 in leaders)
        }
    }
}
