/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

/**
 * Sums [this] positive [Int] with an[other], and returns the result only if the result is positive.
 * Otherwise, the function assumes that the sum has overflowed and returns [Int.MAX_VALUE].
 */
fun Int.nonOverflowingPlus(other: Int): Int {
    require(this >= 0 && other >= 0) {
        "Both operands must be positive integers."
    }
    return when (val sum = this + other) {
        in 0..Int.MAX_VALUE -> sum
        else -> Int.MAX_VALUE
    }
}
