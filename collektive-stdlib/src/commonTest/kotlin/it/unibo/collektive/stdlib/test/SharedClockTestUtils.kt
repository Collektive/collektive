/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.sharedClock
import it.unibo.collektive.stdlib.sharedClockWithLag
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.time.Duration.Companion.seconds

abstract class SharedClockTestUtils {

    /**
     * Verifies that the node with the specified [nodeId] in the environment has the given [time] as its value.
     *
     * @param nodeId the ID of the node whose value is being verified.
     * @param time the expected value of the node, represented as an Instant.
     */
    fun <R> Environment<R>.shouldBeInstant(nodeId: Int, time: Instant) {
        status()[nodeId] shouldBe time
    }

    /**
     * Creates a grid environment of the specified size where each node has a shared clock with execution frequency adjustment.
     *
     * @param size the size of the grid (both width and height).
     * @param frequency the frequency at which to increase the clock value of each node.
     */
    fun gridWithExecutionFrequency(size: Int, frequency: Int, testWithLag: Boolean = false) = mooreGrid<Instant>(size, size, { _, _ ->
        DISTANT_PAST
    }) {
        when {
          testWithLag -> sharedClockWithLag(times[localId]).also { increaseTime(localId, frequency) }
          else -> sharedClock(times[localId]).also { increaseTime(localId, frequency) }
        }
    }

    /**
     * Increases the time for a specific node by a given frequency.
     *
     * @param node The identifier of the node whose time is to be increased.
     * @param frequency The frequency in seconds to be added to the node's current time.
     */
    fun increaseTime(node: Int, frequency: Int) {
        times[node] = times[node] + frequency.seconds
    }

    /**
     * Executes the specified number of subsequent rounds in the environment.
     * Each round runs a cycle for all the nodes in the environment,
     * following the order of node IDs from the lowest to the highest.
     *
     * @param times the number of rounds to execute. Defaults to 1 if no value is provided.
     */
    fun Environment<Instant>.executeSubsequentRounds(times: Int = 1) {
        repeat(times) {
            cycleInOrder()
        }
    }

    /**
     * Executes a staggered sequence of cycles across the nodes in the environment.
     *
     * The execution sequence is as follows:
     * d0 -> (d1 -> d0) -> (d2 -> d1) -> (d3 -> d2) -> d3 -> d0 -> d1 -> d2 -> d3
     *
     * This method performs the following operations:
     * 1. Iterates through all nodes in the environment, cycling the current node and,
     *    if applicable, the immediately preceding node at the same timestamp (in that order).
     * 2. Ensures the last node in the environment also completes a cycle in sequence.
     * 3. Adjusts the execution frequency of each node to align its timing with the
     *    execution time of the last node that cycled.
     * 4. Proceeds to run a complete collective cycle across all nodes in sequential order.
     */
    fun Environment<Instant>.executeStaggeredRounds() {
        // execution sequence: d0 -> (d1 -> d0) -> (d2 -> d1) -> (d3 -> d2) -> d3 -> d0 -> d1 -> d2 -> d3
        for (n in 0 until NUM_DEVICES) {
            nodes.elementAt(n).cycle()
            if (n > 0) nodes.elementAt(n - 1).cycle()
        }
        nodes.last().cycle()
        // change the device's execution frequency to execute after the time of the last device that has cycled
        nodes.forEach { node ->
            // minus one second because it has already been added after the first cycle
            increaseTime(node.id, ONE_SEC_FREQUENCY * (SEQUENTIAL_FREQUENCY - ONE_SEC_FREQUENCY))
        }
        cycleInOrder()
    }

    companion object {
        /**
         * The default size used for creating grid-based environments in gossip tests.
         *
         * It typically represents the edge length of a square grid or the number of nodes
         * in a linear grid, depending on the test case. The value is used to initialize
         * the network size for simulation purposes.
         */
        const val SIZE = 2

        /**
         * Represents the total number of devices in a square grid network.
         * This constant is computed as the square of the grid size (`SIZE`),
         * assuming the grid is dimensioned as `SIZE x SIZE`.
         */
        const val NUM_DEVICES = SIZE * SIZE

        /**
         * Represents the frequency, in seconds, of a specific process or operation.
         *
         * This constant is typically used to define intervals or timing for processes
         * that need to occur at a one-second frequency.
         */
        const val ONE_SEC_FREQUENCY = 1

        /**
         * Defines the frequency of sequential operations, represented as the total number of devices
         * in the environment.
         *
         * This constant is used in testing environments to determine the behavior or execution steps
         * relative to the number of devices in a network.
         */
        const val SEQUENTIAL_FREQUENCY = NUM_DEVICES

        /**
         * A mutable list that keeps track of timestamps represented as `Instant`.
         * Can be used to store and manage instances of time within a test scenario
         * or other temporal context.
         */
        val times = mutableListOf<Instant>()
    }
}
