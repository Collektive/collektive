/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
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
class SharedClockTest : SharedClockTestUtils() {

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
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, SEQUENTIAL_FREQUENCY)
        env.executeSubsequentRounds(times = 1)
        val firstCycle = env.status().values.distinct()
        firstCycle.size shouldBe 1
        firstCycle.first() shouldBe DISTANT_PAST
    }
   @Test
    fun `In two subsequent rounds of computation the devices should increase their time by their delta seconds`() {
        // execution sequence: d0 -> d1 -> d2 -> d3 -> d0 -> d1 -> d2 -> d3
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, SEQUENTIAL_FREQUENCY)
        env.executeSubsequentRounds(times = 2)
        env.nodes.forEach { node ->
            env.shouldBeInstant(node.id, DISTANT_PAST + (SEQUENTIAL_FREQUENCY.seconds * (node.id + 1)))
        }
    }

    @Test
    fun `In staggered rounds of computation the devices should increase their time based on the fastest device`() {
        // execution sequence: d0 -> (d1 -> d0) -> (d2 -> d1) -> (d3 -> d2) -> d3 -> d0 -> d1 -> d2 -> d3
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, ONE_SEC_FREQUENCY)
        env.executeStaggeredRounds()
        val expected = listOf(7, 8, 9, 13).map { DISTANT_PAST + it.seconds }
        env.nodes.forEachIndexed { index, node -> env.shouldBeInstant(node.id, expected[index]) }
    }
}
