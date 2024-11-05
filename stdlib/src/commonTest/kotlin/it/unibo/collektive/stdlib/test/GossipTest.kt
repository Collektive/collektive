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
import it.unibo.collektive.stdlib.SelfStabilizingGossip.gossipMax
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid

class GossipTest : StringSpec({

    // A stable gossip means that every device of the network has the same value
    fun Environment<Double>.gossipIsStable(): Boolean =
        status().values.distinct().size == 1

    fun Environment<Double>.gossipResult(): Double =
        status().values.distinct().first()

    fun squareMooreGridWithGossip(size: Int) =
        mooreGrid<Double>(size, size, { _, _ -> Double.NaN }) {
            gossipMax(localId.toDouble()) { a, b -> a.compareTo(b) } // gossip the max localID in the network
        }.apply {
            nodes.size shouldBe (size * size)
            val initial = status().values.distinct()
            initial.size shouldBe 1
            check(initial.first().isNaN()) {
                "Initial status is not NaN, but it is $initial (${initial::class.simpleName})"
            }
        }

    fun linearMooreGridWithGossip(size: Int) =
        mooreGrid<Double>(size, 1, { _, _ -> Double.NaN }) {
            gossipMax(localId.toDouble()) { a, b -> a.compareTo(b) } // gossip the max localID in the network
        }.apply {
            nodes.size shouldBe size
            val initial = status().values.distinct()
            initial.size shouldBe 1
            check(initial.first().isNaN()) {
                "Initial status is not NaN, but it is $initial (${initial::class.simpleName})"
            }
        }

    "gossip in a moore grid stabilizes after 2 reverse cycles" {
        val size = 5
        val environment: Environment<Double> = squareMooreGridWithGossip(size)
        environment.cycleInOrder()
        val firstRound = environment.status()
        // initially there should be different values as many are the devices in the network
        firstRound.values.distinct().size shouldBe (size * size)
        // in this implementation the initial value for each device is its own ID
        firstRound.forEach { (id, value) -> value shouldBe id.toDouble() }
        // status at first cycle
        environment.gossipIsStable() shouldBe false
        environment.cycleInReverseOrder()
        // status at second cycle
        environment.gossipIsStable() shouldBe true
        environment.gossipResult() shouldBe 24.0
    }

    "gossip in the best case stabilizes in one cycle" {
        val size = 5
        val environment: Environment<Double> = squareMooreGridWithGossip(size)
        environment.cycleInReverseOrder()
        environment.gossipIsStable() shouldBe true
        environment.gossipResult() shouldBe 24.0
    }

    "gossip in the worst case stabilizes in the network diameter cycles" {
        val size = 10
        val environment: Environment<Double> = linearMooreGridWithGossip(size)
        repeat(times = size - 1) {
            environment.cycleInOrder()
            environment.gossipIsStable() shouldBe false
        }
        environment.cycleInOrder()
        environment.gossipIsStable() shouldBe true
        environment.gossipResult() shouldBe 9.0
    }
})
