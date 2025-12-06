/*
 * Copyright (c) 2023-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.processes.timeReplicated
import it.unibo.collektive.stdlib.spreading.nonStabilizingGossip
import it.unibo.collektive.stdlib.test.MultiClock.Companion.DEVICE_COUNT
import it.unibo.collektive.stdlib.test.MultiClock.Companion.GRID_SIZE
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class TimeReplicatedTest {

    @Test
    fun `Time replicated gossip should converge after a few rounds`() {
        with(MultiClock(DEVICE_COUNT)) {
            val env: Environment<Int> = gridWithTimeIntervalBetweenRounds()
            env.runAllDevices(times = DEVICE_COUNT)
            val finalStatus = env.status().values.distinct()
            finalStatus.size shouldBe 1
            finalStatus.first() shouldBe env.nodes.maxBy { it.id }.id // should be 3
        }
    }

    @Test
    fun `Time replicated gossip should stabilize after a few rounds from disruptive event`() {
        with(MultiClock(DEVICE_COUNT)) {
            val env: Environment<Int> = gridWithTimeIntervalBetweenRounds()
            env.runAllDevices(times = DEVICE_COUNT)
            val initialStatus = env.status().values.distinct()
            initialStatus.size shouldBe 1
            initialStatus.first() shouldBe env.nodes.maxBy { it.id }.id // should be 3
            env.removeNode(3) // simulate device 3 failure
            env.runAllDevices(times = DEVICE_COUNT * 3)
            val finalStatus = env.status().values.distinct()
            finalStatus.size shouldBe 1
            finalStatus.first() shouldBe env.nodes.maxBy { it.id }.id // should be 2 now
        }
    }

    companion object {
        /**
         * Creates a grid environment of the specified size where each node has a shared clock with execution frequency adjustment.
         */
        context(clock: MultiClock)
        private fun gridWithTimeIntervalBetweenRounds() = mooreGrid<Int>(GRID_SIZE, GRID_SIZE, { _, _ ->
            Int.MIN_VALUE
        }) {
            timeReplicated(currentTime = clock[localId], maxReplicas = 6, timeToSpawn = 6.seconds) {
                nonStabilizingGossip(value = localId, reducer = ::maxOf)
            }.also { clock.increaseTime(localId, DEVICE_COUNT) }
        }
    }
}
