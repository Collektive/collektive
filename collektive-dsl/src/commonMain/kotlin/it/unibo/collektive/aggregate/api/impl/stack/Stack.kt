/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api.impl.stack

import it.unibo.collektive.path.FullPathFactory
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory

/**
 * Simple Stack interface with additional methods for the aggregate computation.
 */
internal interface Stack {
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

    companion object {
        /**
         * Smart constructor for the [Stack] interface.
         */
        internal operator fun invoke(pathFactory: PathFactory = FullPathFactory): Stack = StackDeque(pathFactory)
    }
}
