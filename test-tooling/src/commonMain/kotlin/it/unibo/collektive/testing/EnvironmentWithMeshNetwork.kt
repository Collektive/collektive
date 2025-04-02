/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

/**
 * An environment where nodes are connected if they are within [connectDistance] from each other.
 */
class EnvironmentWithMeshNetwork<R>(val connectDistance: Double) :
    Environment<R>(
        { thisEnv, a, b -> thisEnv.positionOf(a).distanceTo(thisEnv.positionOf(b)) <= connectDistance },
    ) {
    override fun toString() = "Environment(connection=$connectDistance, nodes=$nodes)"
}
