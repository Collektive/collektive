/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

/**
 * A factory that creates [Path] instances by concatenating their content.
 */
object FullPathFactory : PathFactory {
    /**
     * Creates a [Path] instance by concatenating the given [tokens].
     */
    override fun invoke(tokens: List<Any?>): Path = FullPath(tokens)
}
