/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.field.Field
import it.unibo.collektive.stdlib.ints.FieldedInts.toDouble
import it.unibo.collektive.stdlib.util.Accumulator
import it.unibo.collektive.stdlib.util.hops
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
    isRiemannianManifold: Boolean = true,
    metric: Field<ID, Double> = hops().toDouble(),
): Double = distanceTo(source, 0.0, Double.POSITIVE_INFINITY, metric, maxPaths, isRiemannianManifold, Double::plus)

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
    metric: Field<ID, Distance>,
    maxPaths: Int = Int.MAX_VALUE,
    isRiemannianManifold: Boolean = true,
    noinline accumulateDistance: Accumulator<Distance>,
): Distance = gradientCast(
    source = source,
    local = if (source) bottom else top,
    bottom = bottom,
    top = top,
    metric = metric,
    maxPaths = maxPaths,
    isRiemannianManifold = isRiemannianManifold,
    accumulateData = { neighborToSource, hereToNeighbor, _ ->
        accumulateDistance(neighborToSource, hereToNeighbor)
    },
    accumulateDistance = accumulateDistance,
)

/**
 * Computes the hop distance from the closest [source].
 */
@JvmOverloads
inline fun <reified ID : Any> Aggregate<ID>.hopDistanceTo(source: Boolean, maxPaths: Int = Int.MAX_VALUE): Int =
    hopGradientCast(source = source, local = 0, maxPaths = maxPaths) { neighborToSource, hereToNeighbor, _ ->
        neighborToSource + hereToNeighbor
    }
