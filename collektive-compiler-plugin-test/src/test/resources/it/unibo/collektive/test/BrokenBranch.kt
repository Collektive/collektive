/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.test

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import kotlin.random.Random

fun <ID : Any> Aggregate<ID>.project() {
    if (Random.nextDouble() < 0.5) {
        neighboring(1)
    } else {
        neighboring(2)
    }.alignedMapValues(neighboring(3)) { a, b -> a + b }
}
