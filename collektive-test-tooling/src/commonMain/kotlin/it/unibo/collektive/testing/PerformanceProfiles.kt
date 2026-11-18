/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.aggregate.alignment.PerformanceProfile

/**
 * The [PerformanceProfile]s every alignment-sensitive test should be run against.
 */
val testedPerformanceProfiles: List<PerformanceProfile> = listOf(
    PerformanceProfile.ForSimulation,
    PerformanceProfile.ForDeployment.Fast(),
    PerformanceProfile.ForDeployment.LowMemory.Fast,
    PerformanceProfile.ForDeployment.WithCryptographicPaths(),
)

/**
 * The [PerformanceProfile]s producing serializable paths, for tests requiring network serialization.
 */
val serializablePerformanceProfiles: List<PerformanceProfile> = listOf(
    PerformanceProfile.ForDeployment.Fast(),
    PerformanceProfile.ForDeployment.LowMemory.Fast,
    PerformanceProfile.ForDeployment.LowMemory.JsFriendly,
    PerformanceProfile.ForDeployment.WithCryptographicPaths(),
)

/**
 * A human-readable name for this [PerformanceProfile].
 */
val PerformanceProfile.name: String get() = this::class.simpleName ?: toString()

/**
 * Runs the [test] once for each of the [profiles] (by default, [testedPerformanceProfiles]).
 * Failures are rethrown as [AssertionError]s that report the profile they occurred with.
 */
inline fun forEachPerformanceProfile(
    profiles: List<PerformanceProfile> = testedPerformanceProfiles,
    test: (PerformanceProfile) -> Unit,
) {
    profiles.forEach { profile ->
        runCatching { test(profile) }.onFailure { error ->
            throw AssertionError("Failure with performance profile ${profile.name}: ${error.message}", error)
        }
    }
}
