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
        times.clear()
        val baseTime = Instant.parse("2024-01-01T00:00:00Z")
        repeat(NUM_DEVICES) {
            times += baseTime + it.seconds
        }
    }

    @Test
    fun `Time replicated gossip should converge after a few rounds`() {
        val env: Environment<Int> = gridWithExecutionFrequency(SEQUENTIAL_FREQUENCY)
        env.executeSubsequentRounds(times = NUM_DEVICES)
        val finalStatus = env.status().values.distinct()
        finalStatus.size shouldBe 1
        finalStatus.first() shouldBe env.nodes.maxBy { it.id }.id // should be 3
    }

    @Test
    fun `Time replicated gossip should stabilize after a few rounds from disruptive event`() {
        val env: Environment<Int> = gridWithExecutionFrequency(SEQUENTIAL_FREQUENCY)
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
         * The default size used for creating grid-based environments in gossip tests.
         *
         * It typically represents the edge length of a square grid or the number of nodes
         * in a linear grid, depending on the test case. The value is used to initialize
         * the network size for simulation purposes.
         */
        private const val SIZE = 2

        /**
         * Represents the total number of devices in a square grid network.
         * This constant is computed as the square of the grid size (`SIZE`),
         * assuming the grid is dimensioned as `SIZE x SIZE`.
         */
        private const val NUM_DEVICES = SIZE * SIZE

        /**
         * Defines the frequency of sequential operations, represented as the total number of devices
         * in the environment.
         *
         * This constant is used in testing environments to determine the behavior or execution steps
         * relative to the number of devices in a network.
         */
        private const val SEQUENTIAL_FREQUENCY = NUM_DEVICES

        /**
         * A mutable list that keeps track of timestamps represented as `Instant`.
         * Can be used to store and manage instances of time within a test scenario
         * or other temporal context.
         */
        private val times = mutableListOf<Instant>()

        /**
         * Creates a grid environment of the specified size where each node has a shared clock with execution frequency adjustment.
         *
         * @param frequency the frequency at which to increase the clock value of each node.
         */
        private fun gridWithExecutionFrequency(frequency: Int) = mooreGrid<Int>(SIZE, SIZE, { _, _ ->
            Int.MIN_VALUE
        }) {
            timeReplicated(
                currentTime = times[localId],
                maxReplicas = 4,
                timeToSpawn = 3.seconds,
                process = { nonStabilizingGossip(value = localId, reducer = ::maxOf) },
            ).also { increaseTime(localId, frequency) }
        }

        /**
         * Increases the time for a specific node by a given frequency.
         *
         * @param node The identifier of the node whose time is to be increased.
         * @param frequency The frequency in seconds to be added to the node's current time.
         */
        private fun increaseTime(node: Int, frequency: Int) {
            times[node] = times[node] + frequency.seconds
        }

        /**
         * Executes the specified number of subsequent rounds in the environment.
         * Each round runs a cycle for all the nodes in the environment,
         * following the order of node IDs from the lowest to the highest.
         *
         * @param times the number of rounds to execute. Defaults to 1 if no value is provided.
         */
        private fun Environment<Int>.executeSubsequentRounds(times: Int = 1) {
            repeat(times) {
                cycleInOrder()
            }
        }
    }
}
