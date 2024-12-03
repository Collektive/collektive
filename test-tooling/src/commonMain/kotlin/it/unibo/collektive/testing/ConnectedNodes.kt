/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.aggregate.api.Aggregate

fun <R> connectedGrid(
    sizeX: Int,
    sizeY: Int,
    initial: (Environment<R>, Position) -> R,
    program: Aggregate<Int>.(Environment<R>) -> R,
): FullyConnectedEnvironment<R> =
    FullyConnectedEnvironment<R>()
        .grid(sizeX, sizeY, initial, program)