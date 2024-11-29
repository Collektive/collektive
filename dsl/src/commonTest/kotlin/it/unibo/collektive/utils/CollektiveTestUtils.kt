/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.utils

/**
 * Utilities for testing Collektive.
 */
object CollektiveTestUtils {
    /**
     * Performs [steps] rounds of the computation defined by [block], starting from [initial].
     */
    fun <Result> roundFor(
        steps: Int = 10,
        initial: Result,
        block: (Result) -> Result,
    ): Result = (0 until steps).fold(initial) { acc, _ -> block(acc) }
}
