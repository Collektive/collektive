/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.spreading.nonStabilizingGossip
import it.unibo.collektive.stdlib.timeReplicated
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlinx.datetime.Instant
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimeReplicatedTest {

    @BeforeTest
    fun setup() { // before each test
        setupTimes()
    }

    @Test
    fun `Time replicated gossip should converge after a few rounds`() {
        val env: Environment<Int> = gridWithExecutionFrequency()
        env.executeSubsequentRounds(times = NUM_DEVICES)
        val finalStatus = env.status().values.distinct()
        finalStatus.size shouldBe 1
        finalStatus.first() shouldBe env.nodes.maxBy { it.id }.id // should be 3
    }

    @Test
    fun `Time replicated gossip should stabilize after a few rounds from disruptive event`() {
        val env: Environment<Int> = gridWithExecutionFrequency()
        env.executeSubsequentRounds(times = NUM_DEVICES)
        env.status().values.distinct()
        env.removeNode(3) // simulate device 3 failure
        env.executeSubsequentRounds(times = NUM_DEVICES * 2)
        val finalStatus = env.status().values.distinct()
        finalStatus.size shouldBe 1
        finalStatus.first() shouldBe env.nodes.maxBy { it.id }.id // should be 2 now
    }

    companion object {
        /**
         * Creates a grid environment of the specified size where each node has a shared clock with execution frequency adjustment.
         */
        private fun gridWithExecutionFrequency() = mooreGrid<Int>(GRID_SIZE, GRID_SIZE, { _, _ ->
            Int.MIN_VALUE
        }) {
            timeReplicated(
                currentTime = times[localId],
                maxReplicas = 4,
                timeToSpawn = 3.seconds,
                process = { nonStabilizingGossip(value = localId, reducer = ::maxOf) },
            ).also { increaseTime(localId, SEQUENTIAL_FREQUENCY) }
        }
    }
}
