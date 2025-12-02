/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test
import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.test.MultiClock.Companion.DEVICE_COUNT
import it.unibo.collektive.stdlib.test.MultiClock.Companion.ONE_SECOND
import it.unibo.collektive.stdlib.time.sharedClock
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST

@OptIn(ExperimentalTime::class)
class SharedClockTest {

    @Test
    fun `After a single round of computation all devices should return DISTANT_PAST`() {
        // execution sequence: d0 -> d1 -> d2 -> d3
        with(MultiClock(DEVICE_COUNT)) {
            val env: Environment<Instant> = gridWithTimeIntervalBetweenRounds()
            env.cycleInOrder()
            val firstCycle = env.status().values.distinct()
            firstCycle.size shouldBe 1
            firstCycle.first() shouldBe DISTANT_PAST
        }
    }

    @Test
    fun `In two subsequent rounds of computation the devices should increase their time by their delta seconds`() {
        // execution sequence: d0 -> d1 -> d2 -> d3 -> d0 -> d1 -> d2 -> d3
        with(MultiClock(DEVICE_COUNT)) {
            val env: Environment<Instant> = gridWithTimeIntervalBetweenRounds()
            env.runAllDevices(times = 2)
            val twoRounds = env.status().values.distinct()
            twoRounds.size shouldBe 1
            twoRounds.first() shouldBe DISTANT_PAST + DEVICE_COUNT.seconds
        }
    }

    @Test
    fun `In staggered rounds of computation the devices should increase their time based on the fastest device`() {
        // execution sequence: d0 -> (d1 -> d0) -> (d2 -> d1) -> (d3 -> d2) -> d3 -> d0 -> d1 -> d2 -> d3
        with(MultiClock(DEVICE_COUNT)) {
            val env: Environment<Instant> = gridWithTimeIntervalBetweenRounds(timeBetweenRounds = ONE_SECOND)
            for (n in 0 until DEVICE_COUNT) {
                env.nodes.elementAt(n).cycle()
                if (n > 0) env.nodes.elementAt(n - 1).cycle()
            }
            env.nodes.last().cycle()
            // change the device's execution frequency to execute after the time of the last device that has cycled
            env.nodes.forEach { node ->
                // minus one second because it has already been added after the first cycle
                increaseTime(node.id, ONE_SECOND * (DEVICE_COUNT - ONE_SECOND))
            }
            env.cycleInOrder()
            val offset = env.nodes.size + ONE_SECOND // each device gets an additional offset per round
            val expected = listOf(offset, offset, offset + ONE_SECOND, offset + ONE_SECOND)
            env.nodes.forEachIndexed { index, node ->
                env.shouldBeInstant(
                    node.id,
                    DISTANT_PAST + expected[index].seconds,
                )
            }
        }
    }

    @Test
    fun `A single device that execute with a higher timestamp should put forward the time`() {
        // execution sequence: d0 -> d1 -> d2 -> d3 -> d0 -> d1 -> d2 -> d3 ->
        // -> d0 (drift time) -> d1 -> d2 -> d3 -> d1 -> d2 -> d3 (d0 is disappeared)
        with(MultiClock(DEVICE_COUNT)) {
            val env: Environment<Instant> = gridWithTimeIntervalBetweenRounds()
            val subsequentRounds = 2
            env.runAllDevices(times = subsequentRounds)
            val node = env.nodes.find { it.id == 0 }
            // put device 0 at virtual time 20
            // (it was already at virtual time 8, as the timestamp has been increased twice by SEQUENTIAL_FREQUENCY)
            val driftTime = DEVICE_COUNT * 3
            increaseTime(node!!.id, driftTime)
            // execute a single round with the fast-forwarded device
            env.runAllDevices(times = 1)
            // simulate the death of device 0
            env.nodes.filter { it.id != node.id }.forEach { n -> n.cycle() }
            val deadDeviceTime = (DEVICE_COUNT * subsequentRounds) + driftTime
            val inSynch = deadDeviceTime + DEVICE_COUNT
            val expected = listOf(deadDeviceTime, inSynch, inSynch, inSynch)
            env.nodes.forEachIndexed { index, node ->
                env.shouldBeInstant(
                    node.id,
                    DISTANT_PAST + expected[index].seconds,
                )
            }
        }
    }

    companion object {
        /**
         * Verifies that the node with the specified [nodeId] in the environment has the given [time] as its value.
         *
         * @param nodeId the ID of the node whose value is being verified.
         * @param time the expected value of the node, represented as an Instant.
         */
        private fun <R> Environment<R>.shouldBeInstant(nodeId: Int, time: Instant) {
            status()[nodeId] shouldBe time
        }

        context(clock: MultiClock)
        private fun gridWithTimeIntervalBetweenRounds(sideSize: Int = 2, timeBetweenRounds: Int = sideSize * sideSize) =
            mooreGrid<Instant>(2, 2, { _, _ -> DISTANT_PAST }) {
                sharedClock(clock[localId]).also { clock.increaseTime(localId, timeBetweenRounds) }
            }
    }
}
