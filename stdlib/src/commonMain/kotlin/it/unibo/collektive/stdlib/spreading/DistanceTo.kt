/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.field.Field
import kotlin.jvm.JvmOverloads

/**
 * Compute the distance from the closest [source], using [Double]s.
 *
 * The distance between neighboring devices is computed using the [metric] function,
 * and defaults to the hop distance.
 */
@JvmOverloads
inline fun <reified ID : Any> Aggregate<ID>.distanceTo(
    source: Boolean,
    maxPaths: Int = Int.MAX_VALUE,
    crossinline metric: () -> Field<ID, Double> = { neighboring(1.0) },
): Double = distanceTo(source, 0.0, Double.POSITIVE_INFINITY, maxPaths, Double::plus, metric)

/**
 * Compute the [Distance] from the closest [source], starting from [bottom] and up to [top].
 *
 * the [Distance] between neighboring devices is computed using the [metric] function,
 * the distance summation is governed by the [accumulateDistance] function.
 */
inline fun <reified ID : Any, reified Distance : Comparable<Distance>> Aggregate<ID>.distanceTo(
    source: Boolean,
    bottom: Distance,
    top: Distance,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateDistance: (Distance, Distance) -> Distance,
    crossinline metric: () -> Field<ID, Distance>,
): Distance = gradientCast(
    source = source,
    local = if (source) bottom else top,
    bottom = bottom,
    top = top,
    maxPaths = maxPaths,
    accumulateData = { neighborToSource, hereToNeighbor, _ ->
        accumulateDistance(neighborToSource, hereToNeighbor)
    },
    accumulateDistance = accumulateDistance,
    metric = metric,
)

/**
 * Computes the hop distance from the closest [source].
 */
@JvmOverloads
inline fun <reified ID : Any> Aggregate<ID>.hopDistanceTo(source: Boolean, maxPaths: Int = Int.MAX_VALUE): Int =
    distanceTo(source, 0, Int.MAX_VALUE, maxPaths, Int::plus) { neighboring(1) }
