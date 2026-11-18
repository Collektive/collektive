/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment

/**
 * Simple Stack interface with additional methods for the aggregate computation.
 */
interface Stack {
    /**
     * Returns the current path of the stack.
     */
    fun currentPath(): Path

    /**
     * Pushes the [token] in the stack.
     */
    fun alignRaw(token: Any?)

    /**
     * Pops the last element of the stack.
     */
    fun dealign()
}
