/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import kotlinx.serialization.Serializable
import kotlin.math.sqrt

/**
 * A position in a 3D space with [x], [y], and [z] coordinates.
 */
@Serializable
data class Position(val x: Double, val y: Double, val z: Double) {
    /**
     * Returns the Euclidean distance between this position and [other].
     */
    fun distanceTo(other: Position): Double {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }

    /**
     * Returns the Euclidean distance between this position and the [x], [y], and [z] coordinates.
     */
    fun toTriple(): Triple<Double, Double, Double> = Triple(x, y, z)

    /**
     * Conversion functions to and from [Position].
     */
    companion object {
        /**
         * Creates a [Position] from a 3D coordinate.
         */
        fun Triple<Double, Double, Double>.asPosition(): Position = Position(first, second, third)

        /**
         * Creates a [Position] from a 2D coordinate with a default z value of 0.0.
         */
        fun Pair<Double, Double>.asPosition(z: Double = 0.0): Position = Position(first, second, z)
    }
}
