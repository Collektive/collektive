/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.benchmarks

import it.unibo.collektive.stdlib.spreading.distanceTo
import it.unibo.collektive.stdlib.spreading.gossipMax
import it.unibo.collektive.testing.Environment
import it.unibo.collektive.testing.mooreGrid
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Measures a full round of a realistic aggregate program (a gradient plus a gossip)
 * on a [size] by [size] Moore grid, with in-memory message passing.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
class ProgramBenchmark {

    /**
     * Name of the profile under test, see [profiles].
     */
    @Param(
        "ForSimulation",
        "ForSimulationPersistentList",
        "ForSimulationConsList",
        "ForSimulationConsListFnv1a32",
        "ForSimulationArrayList",
        "ForSimulationArrayListFnv1a32",
        "SafeDeployment",
        "ForDeploymentFast",
        "LowMemoryFast",
        "LowMemoryJsFriendly",
        "LowMemoryKeccak512",
    )
    var profile: String = ""

    /**
     * Side of the grid.
     */
    @Param("10")
    var size: Int = 0

    private lateinit var environment: Environment<Double>

    /**
     * Builds the grid and lets the program stabilize.
     */
    @Setup
    fun setup() {
        environment = mooreGrid(size, size, Double.POSITIVE_INFINITY, profiles.getValue(profile)) { _ ->
            distanceTo(localId == 0) + gossipMax(localId)
        }
        repeat(size * 2) { environment.cycleInOrder() }
        val expected = (size * size - 1).toDouble()
        check(environment.status()[0] == expected) {
            "The program did not stabilize with $profile: ${environment.status()[0]} instead of $expected"
        }
    }

    /**
     * One round of every device in the grid.
     */
    @Benchmark
    fun round() {
        environment.cycleInOrder()
    }
}
