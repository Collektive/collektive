/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.sharedClock
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SharedClockTest {

    @BeforeTest
    fun setup() { // before each test
        repeat(NUM_DEVICES) {
            times += Instant.parse(
                input = "2024-01-01T00:00:0$it.00Z",
            )
        }
    }

    fun <R> Environment<R>.shouldBeInstant(nodeId: Int, time: Instant) {
        status()[nodeId] shouldBe time
    }

    fun gridWithExecutionFrequency(size: Int, frequency: Int) = mooreGrid<Instant>(size, size, { _, _ -> DISTANT_PAST }) {
        sharedClock(times[localId]).also { increaseTime(localId, frequency) }
    }

    fun increaseTime(node: Int, frequency: Int) {
        times[node] = times[node] + frequency.seconds
    }

    @Test
    fun `After a single round of computation, all devices should return DISTANT_PAST`() {
        // execution sequence: d0 -> d1 -> d2 -> d3
        val environment: Environment<Instant> = gridWithExecutionFrequency(SIZE, SEQUENTIAL_FREQUENCY)
        environment.cycleInOrder()
        val firstCycle = environment.status().values.distinct()
        firstCycle.size shouldBe 1
        firstCycle.first() shouldBe DISTANT_PAST
    }

    @Test
    fun `In two subsequent rounds of computation, the devices should increase their time by their delta seconds`() {
        // execution sequence: d0 -> d1 -> d2 -> d3 -> d0 -> d1 -> d2 -> d3
        val environment: Environment<Instant> = gridWithExecutionFrequency(SIZE, SEQUENTIAL_FREQUENCY)
        environment.cycleInOrder()
        environment.cycleInOrder()
        environment.nodes.forEach { node ->
            environment.shouldBeInstant(node.id, DISTANT_PAST + (SEQUENTIAL_FREQUENCY.seconds * (node.id + 1)))
        }
    }

    @Test
    fun `In staggered rounds of computation, the devices should increase their time based on the fastest device`() {
        // execution sequence: d0 -> (d1 -> d0) -> (d2 -> d1) -> (d3 -> d2) -> d3 -> d0 -> d1 -> d2 -> d3
        val env: Environment<Instant> = gridWithExecutionFrequency(SIZE, ONE_SEC_FREQUENCY)
        for (n in 0 until NUM_DEVICES) {
            env.nodes.elementAt(n).cycle()
            if (n > 0) env.nodes.elementAt(n - 1).cycle()
        }
        env.nodes.last().cycle()
        // change the device's execution frequency to execute after the time of the last device that has cycled
        env.nodes.forEach { node ->
            // minus one second because it has already been added after the first cycle
            increaseTime(node.id, ONE_SEC_FREQUENCY * (SEQUENTIAL_FREQUENCY - ONE_SEC_FREQUENCY))
        }
        env.cycleInOrder()
        val expected = listOf(7, 8, 9, 13).map { DISTANT_PAST + it.seconds }
        env.nodes.forEachIndexed { index, node -> env.shouldBeInstant(node.id, expected[index]) }
    }

    companion object {
        const val SIZE = 2
        const val NUM_DEVICES = SIZE * SIZE
        const val ONE_SEC_FREQUENCY = 1
        const val SEQUENTIAL_FREQUENCY = NUM_DEVICES
        val times = mutableListOf<Instant>()
    }
}
