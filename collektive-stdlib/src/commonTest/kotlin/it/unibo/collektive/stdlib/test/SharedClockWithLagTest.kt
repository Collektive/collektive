/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.matchers.shouldBe
import it.unibo.collektive.testing.Environment
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
class SharedClockWithLagTest : SharedClockTestUtils() {

    @BeforeTest
    fun setup() { // before each test
        times.clear()
        val baseTime = Instant.parse("2024-01-01T00:00:00Z")
        repeat(NUM_DEVICES) {
            times += baseTime + it.seconds
        }
    }

    @Test
    fun `After a single round of computation all devices should return DISTANT_PAST`() {
        // execution sequence: d0 -> d1 -> d2 -> d3
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, SEQUENTIAL_FREQUENCY, testWithLag = true)
        env.executeSubsequentRounds(times = 1)
        env.status().values.size shouldBe 4
        env.nodes.forEach { node ->
            env.shouldBeInstant(node.id, DISTANT_PAST + node.id.seconds)
        }
    }

    @Test
    fun `In two subsequent rounds of computation the devices should increase their time by their delta seconds`() {
        // execution sequence: d0 -> d1 -> d2 -> d3 -> d0 -> d1 -> d2 -> d3
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, SEQUENTIAL_FREQUENCY, testWithLag = true)
        env.executeSubsequentRounds(times = 2)
        env.nodes.forEach { node ->
            env.shouldBeInstant(node.id, DISTANT_PAST + node.id.seconds + env.nodes.size.seconds)
        }
    }

    @Test
    fun `In staggered rounds of computation the devices should increase their time based on the fastest device`() {
        // execution sequence: d0 -> (d1 -> d0) -> (d2 -> d1) -> (d3 -> d2) -> d3 -> d0 -> d1 -> d2 -> d3
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, ONE_SEC_FREQUENCY, testWithLag = true)
        env.executeStaggeredRounds()
        env.nodes.forEach { node ->
            env.shouldBeInstant(node.id, DISTANT_PAST + 1.seconds + node.id.seconds + env.nodes.size.seconds)
        }
    }
}
