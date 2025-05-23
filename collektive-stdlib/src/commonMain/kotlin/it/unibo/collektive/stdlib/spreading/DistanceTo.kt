/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.stdlib.ints.FieldedInts.toDouble
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.hops
import kotlin.jvm.JvmOverloads

/**
 * Computes the distance from the nearest source node as a `Double`.
 *
 * The distance between neighboring devices is computed using the `metric` function
 * and defaults to the hop-count metric.
 *
 * @param ID The type used for neighbor identifiers.
 * @param source True if this device is a source node; otherwise false.
 * @param metric A `Field<ID, Double>` providing edge weights to each neighbor. Defaults to `hops().toDouble()`.
 * @param maxDiameter Maximum allowed hops before discarding paths. Defaults to `Int.MAX_VALUE`.
 * @return The computed `Double` distance to the closest source.
 */
@JvmOverloads
inline fun <reified ID : Any> Aggregate<ID>.distanceTo(
    source: Boolean,
    metric: Field<ID, Double> = hops().toDouble(),
    maxDiameter: Int = Int.MAX_VALUE,
): Double = distanceTo(source, 0.0, Double.POSITIVE_INFINITY, metric, maxDiameter, Double::plus)

/**
 * Computes the distance from the nearest source node within a specified range.
 *
 * Starting from `bottom` at a source and up to `top`, distances between neighboring devices
 * are computed using the `metric` function and accumulated via `accumulateDistance`.
 *
 * @param ID The type used for neighbor identifiers.
 * @param Distance The comparable type representing path lengths.
 * @param source True if this device is a source node; otherwise false.
 * @param bottom The zero-distance value at a source.
 * @param top The maximum allowed distance; incoming distances are clamped to this value.
 * @param metric A `Field<ID, Distance>` providing edge weights to each neighbor.
 * @param maxDiameter Maximum allowed hops before discarding paths. Defaults to `Int.MAX_VALUE`.
 * @param accumulateDistance Reducer function to combine two distances.
 * @return The computed `Distance` to the closest source.
 */
inline fun <reified ID : Any, reified Distance : Comparable<Distance>> Aggregate<ID>.distanceTo(
    source: Boolean,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateDistance: Reducer<Distance>,
): Distance = gradientCast(
    source = source,
    local = if (source) bottom else top,
    bottom = bottom,
    top = top,
    metric = metric,
    maxDiameter = maxDiameter,
    accumulateData = { neighborToSource, hereToNeighbor, _ ->
        accumulateDistance(neighborToSource, hereToNeighbor)
    },
    accumulateDistance = accumulateDistance,
)

/**
 * Computes the hop-count distance from the nearest source node as an `Int`.
 *
 * @param ID The type used for neighbor identifiers.
 * @param source True if this device is a source node; otherwise false.
 * @param maxDiameter Maximum allowed hops before discarding paths. Defaults to `Int.MAX_VALUE`.
 * @return The computed hop-count `Int` distance to the closest source.
 */
@JvmOverloads
inline fun <reified ID : Any> Aggregate<ID>.hopDistanceTo(source: Boolean, maxDiameter: Int = Int.MAX_VALUE): Int =
    hopGradientCast(source = source, local = 0, maxDiameter = maxDiameter) { neighborToSource, hereToNeighbor, _ ->
        neighborToSource + hereToNeighbor
    }
