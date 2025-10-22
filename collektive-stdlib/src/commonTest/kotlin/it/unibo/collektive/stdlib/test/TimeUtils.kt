/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import it.unibo.collektive.testing.Environment
import kotlinx.datetime.Instant
import kotlin.time.Duration.Companion.seconds

/**
 * The default size used for creating grid-based environments in gossip tests.
 *
 * It typically represents the edge length of a square grid or the number of nodes
 * in a linear grid, depending on the test case. The value is used to initialize
 * the network size for simulation purposes.
 */
const val GRID_SIZE = 2

/**
 * Represents the total number of devices in a square grid network.
 * This constant is computed as the square of the grid size (`SIZE`),
 * assuming the grid is dimensioned as `SIZE x SIZE`.
 */
const val NUM_DEVICES = GRID_SIZE * GRID_SIZE

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
internal val times = mutableListOf<Instant>()

/**
 * Executes the specified number of subsequent rounds in the environment.
 * Each round runs a cycle for all the nodes in the environment,
 * following the order of node IDs from the lowest to the highest.
 *
 * @param times the number of rounds to execute. Defaults to 1 if no value is provided.
 */
internal fun <Type> Environment<Type>.executeSubsequentRounds(times: Int = 1) {
    repeat(times) {
        cycleInOrder()
    }
}

internal fun setupTimes() {
    times.clear()
    val baseTime = Instant.parse("2024-01-01T00:00:00Z")
    repeat(NUM_DEVICES) {
        times += baseTime + it.seconds
    }
}

/**
 * Increases the time for a specific node by a given frequency.
 *
 * @param node The identifier of the node whose time is to be increased.
 * @param frequency The frequency in seconds to be added to the node's current time.
 */
internal fun increaseTime(node: Int, frequency: Int) {
    times[node] = times[node] + frequency.seconds
}
