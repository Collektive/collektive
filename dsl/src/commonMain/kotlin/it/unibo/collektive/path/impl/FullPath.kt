/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path.impl

import it.unibo.collektive.path.Path

/**
 * Non-serializable, list-based, high performance path intended for use in simulated environments.
 * Unsuitable for practical applications.
 */
data class FullPath(
    private val path: List<Any?>,
) : Path {
    private val hash = path.hashCode()

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            other !is FullPath -> false
            else -> hash == other.hashCode() && path == other.path
        }

    override fun toString(): String = path.joinToString(separator = "/")

    /**
     * Utility companion object.
     */
    companion object {
        private val cache = mutableMapOf<List<Any?>, Path>()
        private const val MAX_CACHE_SIZE = 10_000
        private const val CACHE_CLEANUP_SIZE = 1000

        /**
         * Creates a new [Path] from the given [path].
         */
        fun of(path: List<Any?>): Path =
            cache.getOrPut(path) { FullPath(path) }.also {
                if (cache.size >= MAX_CACHE_SIZE) {
                    val iterator = cache.iterator()
                    repeat(CACHE_CLEANUP_SIZE) {
                        iterator.next()
                        iterator.remove()
                    }
                }
            }
    }
}
