/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.networking.NoNeighborsData

/**
 * Utility object for round-based computation testing.
 */
object Round {
    /**
     * Simulates a round-based computation for [steps] rounds for the given [deviceId]
     * executing the [computation] function.
     */
    fun <ID : Any, Result> roundFor(
        steps: Int = 10,
        deviceId: ID,
        computation: Aggregate<ID>.() -> Result,
    ): AggregateResult<ID, Result> {
        require(steps > 0) { "Unable to perform '$steps' rounds. At least 1 round is required" }
        val firstRoundResult = aggregate(deviceId, inMemory = true, compute = computation)
        return (1 until steps).fold(firstRoundResult) { previousResult, _ ->
            aggregate(deviceId, previousResult.newState, NoNeighborsData(), inMemory = true, compute = computation)
        }
    }
}
