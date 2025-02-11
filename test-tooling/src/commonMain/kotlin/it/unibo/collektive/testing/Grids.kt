/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.aggregate.api.Aggregate

/**
 * An environment with a regular grid of nodes of [sizeX] by [sizeY] dimensions.
 * Each node is initialized with the [initial] function and runs the [program] function.
 */
fun <R, E : Environment<R>> E.grid(
    sizeX: Int,
    sizeY: Int,
    initial: (Environment<R>, Position) -> R,
    program: Aggregate<Int>.(Environment<R>, id: Int) -> R,
) = apply {
    for (x in 0 until sizeX) {
        for (y in 0 until sizeY) {
            val position = Position(x.toDouble(), y.toDouble(), 0.0)
            addNode(position, initial(this, position), program)
        }
    }
}

private const val SAFE_MOORE_DISTANCE = 1.5

/**
 * Creates a grid environment with Moore connections between nodes.
 */
fun <R> mooreGrid(
    sizeX: Int,
    sizeY: Int,
    initial: (Environment<R>, Position) -> R,
    program: Aggregate<Int>.(Environment<R>, id: Int) -> R,
): EnvironmentWithMeshNetwork<R> =
    EnvironmentWithMeshNetwork<R>(
        SAFE_MOORE_DISTANCE,
    ).grid(sizeX, sizeY, initial, program)

/**
 * Creates a grid environment with Von Neumann connections between nodes.
 */
fun <R> vonNeumannGrid(
    sizeX: Int,
    sizeY: Int,
    initial: (Environment<R>, Position) -> R,
    program: Aggregate<Int>.(Environment<R>, id: Int) -> R,
) = EnvironmentWithMeshNetwork<R>(1.0).grid(sizeX, sizeY, initial, program)
