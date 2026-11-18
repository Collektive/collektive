/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.benchmarks

import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.PerformanceProfile
import kotlinx.benchmark.Benchmark
import kotlinx.benchmark.BenchmarkMode
import kotlinx.benchmark.BenchmarkTimeUnit
import kotlinx.benchmark.Blackhole
import kotlinx.benchmark.Mode
import kotlinx.benchmark.OutputTimeUnit
import kotlinx.benchmark.Param
import kotlinx.benchmark.Scope
import kotlinx.benchmark.Setup
import kotlinx.benchmark.State

/**
 * Measures the alignment machinery in isolation, mimicking the access pattern of a round:
 * at each nesting level, [FANOUT] sibling constructs are aligned, each one requests its path,
 * looks it up in the previous round state, and stores it in the new state;
 * the last sibling nests further, up to [depth] levels.
 */
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MICROSECONDS)
class StackBenchmark {

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
     * Maximum nesting depth of the simulated program.
     */
    @Param("8", "32")
    var depth: Int = 0

    private lateinit var performanceProfile: PerformanceProfile
    private lateinit var previousState: Map<Path, Int>
    private val tokens = List(FANOUT) { "it.unibo.collektive.aggregate.api.neighboring.$it" }

    /**
     * Resolves the profile and runs a first round to populate the previous state.
     */
    @Setup
    fun setup() {
        performanceProfile = profiles.getValue(profile)
        previousState = round(emptyMap())
    }

    /**
     * One round of the simulated program.
     */
    @Benchmark
    fun alignmentRound(blackhole: Blackhole) {
        blackhole.consume(round(previousState))
    }

    private fun round(previous: Map<Path, Int>): Map<Path, Int> {
        val stack = performanceProfile()
        val state = HashMap<Path, Int>()
        var found = 0
        repeat(depth) { level ->
            tokens.forEachIndexed { index, token ->
                stack.alignRaw(token)
                stack.alignRaw(index)
                val path = stack.currentPath()
                if (previous.containsKey(path)) found++
                state[path] = level
                if (index < FANOUT - 1) {
                    stack.dealign()
                    stack.dealign()
                }
            }
        }
        repeat(depth) {
            stack.dealign()
            stack.dealign()
        }
        check(previous.isEmpty() || found == state.size) { "Misalignment: $found/${state.size} paths found" }
        return state
    }

    private companion object {
        const val FANOUT = 4
    }
}
