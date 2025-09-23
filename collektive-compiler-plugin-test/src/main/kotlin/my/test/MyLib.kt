/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package my.test

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.mapNeighborhood
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.values
import it.unibo.collektive.stdlib.collapse.min
import it.unibo.collektive.stdlib.doubles.FieldedDoubles.plus
import kotlin.Double.Companion.POSITIVE_INFINITY

fun Aggregate<Int>.gradient(source: Boolean): Double = share(POSITIVE_INFINITY) {
    val dist = mapNeighborhood { 1.0 }
    when {
        source -> 0.0
        else -> (it + dist).neighbors.values.min()
    }
}
