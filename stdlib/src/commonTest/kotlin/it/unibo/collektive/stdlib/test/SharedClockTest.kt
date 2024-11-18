/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.test

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import it.unibo.collektive.stdlib.sharedClock
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.DISTANT_PAST

class SharedClockTest : StringSpec({

    fun <Value> Environment<Value>.sharedClockIsStable(): Boolean =
        status().values.distinct().size == 1

    fun squareMooreGridWithSharedClock(size: Int) =
        mooreGrid<Instant>(size, size, { _, _ -> DISTANT_PAST }) {
            sharedClock()
        }.apply {
            nodes.size shouldBe size * size
            val initial = status().values.distinct()
            initial.size shouldBe 1
            check(initial.first() == DISTANT_PAST) {
                "Initial status is not `DISTANT_PAST`, but it is $initial (${initial::class.simpleName})"
            }
        }

    "SharedClock should stabilize in one cycle even if the nodes have different times" {
        val size = 5
        val environment: Environment<Instant> = squareMooreGridWithSharedClock(size)
        generateSequence(0) { it + 1 }.take(environment.nodes.size).forEach { iteration ->
            environment.nodes.drop(iteration).forEach { n -> n.cycle() }
        }
        environment.cycleInReverseOrder()
        environment.sharedClockIsStable().shouldBeTrue()
    }
})
