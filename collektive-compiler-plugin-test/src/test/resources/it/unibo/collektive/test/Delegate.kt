/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.test

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.share
import kotlin.random.Random

val random = kotlin.random.Random(0)

fun <ID : Any> it.unibo.collektive.aggregate.api.Aggregate<ID>.usingDelegate() {
    share(0) {
        val neighbors by kotlin.lazy { it.neighbors }
        if (random.nextInt() % 2 == 0) {
            neighbors.size
        } else {
            0
        }
    }
}
