/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.sharedTimer
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlin.time.Duration
import kotlin.time.Duration.Companion.INFINITE
import kotlin.time.DurationUnit.SECONDS
import kotlin.time.toDuration

class SharedTimerTest : StringSpec({

    val timer = TimeUtils

    fun <Value> Environment<Value>.sharingIsStable(): Boolean =
        status().values.distinct().size == 1

    fun squareMooreGridWithSharedTimer(size: Int, timeToLive: Duration) =
        mooreGrid<Duration>(size, size, { _, _ -> INFINITE }) {
            sharedTimer(timeToLive, processTime = timer.getDeltaTime())
        }.apply {
            nodes.size shouldBe size * size
            val initial = status().values.distinct()
            initial.size shouldBe 1
            check(initial.first() == INFINITE) {
                "Initial status is not `INFINITE`, but it is $initial (${initial::class.simpleName})"
            }
        }

    "sharingTime should stabilize in one cycle even if all nodes have different values" {
        val size = 5
        val timeToLive: Duration = 5.toDuration(SECONDS)
        val environment: Environment<Duration> = squareMooreGridWithSharedTimer(size, timeToLive)
        generateSequence(0) { it + 1 }.take(environment.nodes.size).forEach { iteration ->
            environment.nodes.drop(iteration).forEach { n -> n.cycle() }
        }
        environment.cycleInReverseOrder()
        environment.sharingIsStable() shouldBe true
    }
})
