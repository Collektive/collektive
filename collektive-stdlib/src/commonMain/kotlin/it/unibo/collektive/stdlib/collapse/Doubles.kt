/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import it.unibo.collektive.aggregate.Collapse
import it.unibo.collektive.aggregate.CollapseNeighbors

/**
 * Returns the maximum of all values in this collapse, or [Double.NEGATIVE_INFINITY] if the collapse
 * is an empty [it.unibo.collektive.aggregate.CollapseNeighbors].
 */
fun CollapseNeighbors<Double>.max(): Double = max(Double.NEGATIVE_INFINITY)

/**
 * Returns the minimum of all values in this collapse, or [Double.POSITIVE_INFINITY] if the collapse
 * is an empty [it.unibo.collektive.aggregate.CollapseNeighbors].
 */
fun CollapseNeighbors<Double>.min(): Double = min(Double.POSITIVE_INFINITY)

/**
 * Returns the product of all values in this collapse.
 */
fun Collapse<Double>.product(): Double = sequence.fold(1.0, Double::times)

/**
 * Returns the sum of all values in this collapse.
 */
fun Collapse<Double>.sum(): Double = sequence.fold(0.0, Double::plus)
