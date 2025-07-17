/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.clock
import it.unibo.collektive.stdlib.sharedClock
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class SharedClockTest {

    val size = 2
    val t = (0..size * size).map { Instant.fromEpochSeconds(it.toLong()) }
    var times = emptyList<Instant>().toMutableList()
    val expected = mutableListOf<Instant>()

    @BeforeTest
    fun setup() {
        repeat(size * size) {
            times += Instant.parse(
                input = "2024-01-01T00:00:0$it.00Z",
            )
            expected += (DISTANT_PAST + (4.seconds * (it + 1)))
        }
    }

    fun <R> Environment<R>.sharedClockIsStable(): Boolean = status().values.distinct().size == 1

    fun <R> Environment<R>.shouldBeInstant(nodeId: Int, time: Instant) {
        status()[nodeId] shouldBe time
    }

    fun connectedGridWithSharedClock(size: Int) = mooreGrid<Instant>(size, size, { _, _ -> DISTANT_PAST }) {
        val clock = sharedClock(times[localId])
        times[localId] = times[localId] + 4.seconds
        clock
    }

//        connectedGrid<Instant>(size, size, { _, _ -> DISTANT_PAST }) {
//            val clock = sharedClock(times[localId])
//            times[localId] = times[localId] + 1.seconds
// //            times[localId] = times[localId] + if(localId % 2 == 0) 30.milliseconds else 1.seconds
//            clock
//        }.apply {
//            nodes.size shouldBe size * size
//            val initial = status().values.distinct()
//            initial.size shouldBe 1
//            check(initial.first() == DISTANT_PAST) {
//                "Initial status is not `DISTANT_PAST`, but it is $initial (${initial::class.simpleName})"
//            }
//        }
    @Test
    fun `devices using sharedClock should agree on the current time`() {
        val environment: Environment<Instant> = connectedGridWithSharedClock(size)
//        generateSequence(0) { it + 1 }.take(environment.nodes.size).forEach { iteration ->
//            val dropped = environment.nodes.drop(iteration)
//            dropped.forEach { n ->
//
////                environment.shouldBeInstant(n.id, times[n.id])
//                repeat(n.id + 1) {
//                    n.cycle()
//                }
//            }
//        }
        environment.cycleInOrder()
        println(environment.status())
        environment.cycleInOrder()
        println(environment.status())
        environment.status().values shouldBe expected
    }

    @Test
    fun `SharedClock should stabilize in one cycle even if the nodes have different times`() {
        val environment: Environment<Instant> = connectedGridWithSharedClock(size)
        generateSequence(0) { it + 1 }.take(environment.nodes.size).forEach { iteration ->
            environment.nodes.drop(iteration).forEach { n -> n.cycle() }
        }
        environment.cycleInReverseOrder()
        assertTrue(environment.sharedClockIsStable())
        environment.status().values.distinct() shouldBe Instant.parse("1970-01-01T00:00:00Z")
    }
}
