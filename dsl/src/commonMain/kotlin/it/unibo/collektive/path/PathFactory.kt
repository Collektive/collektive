/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

/**
 * A path represents a specific point in the AST of an aggregate program.
 * The point in the AS is identified as a sequence of tokens.
 */
interface PathFactory {
    /**
     * Creates a new [Path] from the given [tokens].
     */
    operator fun invoke(vararg tokens: Any?): Path = invoke(tokens.toList())

    /**
     * Creates a new [Path] from the given [tokens].
     */
    operator fun invoke(tokens: List<Any?>): Path
}
