/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.ids
import it.unibo.collektive.aggregate.toMap
import it.unibo.collektive.aggregate.values
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmField

/**
 * Filters the input sequence of paths to retain only those that satisfy the coherence criterion.
 * Path coherence ensures that paths containing inconsistent information are removed.
 * Specifically, if a path passes through two nodes in a certain order and another path
 * passes through the same nodes in the reverse order, only the shortest path is retained unless
 * their lengths are identical (indicating a symmetric network).
 *
 * @param nonLoopingPaths The sequence of non-looping paths to be filtered. Each path is represented
 *                        as a [PathValue] containing the path's hops, distance, and associated value.
 * @return A sequence of paths filtered based on the coherence criterion, preserving only valid paths.
 */
@OptIn(DelicateCollektiveApi::class)
@PublishedApi
internal inline fun <reified Distance : Comparable<Distance>, reified ID : Any, reified Value> pathCoherence(
    nonLoopingPaths: Sequence<PathValue<ID, Value, Distance>>,
): Sequence<PathValue<ID, Value, Distance>> {
    val pathsHopSets = nonLoopingPaths.associate { it.nextHop to it.hops.toSet() }
    return nonLoopingPaths.filter { reference ->
        /*
         * Path-coherence: paths that contain inconsistent information must be removed.
         * In particular, if some path passes through A and then B, and another reaches the source
         * through B and then A, we must keep only the shortest
         * (unless they have the same path-length, namely the network is symmetric).
         */
        val refSize = reference.length
        refSize <= 1 ||
            nonLoopingPaths.all { other ->
                // the current reference is shorter
                other.length > refSize ||
                    // same hop count, same distance (symmetric network or same path)
                    (other.length == refSize && other.distance == reference.distance) ||
                    // all common hops appear in the same order
                    reference.allCommonHopsAppearInTheSameOrderOf(other, pathsHopSets)
            }
    }
}

/**
 * Computes all valid non-looping paths emanating from a given node in a network,
 * adhering to specific constraints such as diameter and distance thresholds.
 *
 * @param neighborData A field containing neighbor information and existing paths for each node.
 * @param coercedMetric A field providing distance metrics for the nodes.
 * @param maxDiameter The maximum allowable length for a path.
 * @param bottom The lower distance boundary for filtering paths.
 * @param top The upper distance boundary for filtering paths.
 * @param accumulateData A function to combine data values along the path.
 * @param accumulateDistance A reducer function to combine or calculate distances along the path.
 * @return A sequence of non-looping paths represented as instances of [PathValue],
 * satisfying the specified filtering and accumulation conditions.
 */
@OptIn(DelicateCollektiveApi::class)
@PublishedApi
internal inline fun <
    reified Distance : Comparable<Distance>,
    reified ID : Any,
    reified Value,
    > Aggregate<ID>.nonLoopingPaths(
    neighborData: Field<ID, PathValue<ID, Value, Distance>?>,
    coercedMetric: Field<ID, Distance>,
    maxDiameter: Int,
    bottom: Distance,
    top: Distance,
    crossinline accumulateData: (Distance, Distance, Value) -> Value,
    crossinline accumulateDistance: Reducer<Distance>,
): Sequence<PathValue<ID, Value, Distance>> {
    val neighbors = neighborData.neighbors.ids.set
    val accDistances =
        neighborData.alignedMapValues(coercedMetric) { path, distance ->
            path?.distance?.let { accumulateDistance(it, distance) }
        }
    val neighborAccumulatedDistances = accDistances.neighbors.toMap()
    return neighborData
        .alignedMap(accDistances, coercedMetric) { id, path, accDist, distance ->
            path
                ?.takeUnless { id == localId }
                ?.takeUnless { path.length > maxDiameter }
                ?.takeUnless { localId in path.hops }
                ?.takeUnless { path.isInvalidViaShortcut(accDist, neighbors, neighborAccumulatedDistances) }
                ?.run { accDist to lazy { update(id, distance, bottom, top, accumulateDistance, accumulateData) } }
        }.neighbors.values.sequence.filterNotNull().sortedBy { it.first }.map { it.second.value }
}

/**
 * Represents a path in a graph with a generic identifier for nodes, a value carried through the path,
 * and a distance measure between nodes. This class is used to model paths, allowing comparison based
 * on distance and propagation of updates to include additional hops.
 *
 * @param ID The type of the identifier for nodes in the path.
 * @param Value The type of the data or value carried by the path.
 * @param Distance The type of the distance measure, which must be comparable.
 * @property distance The total distance of the path.
 * @property data The value or data carried along the path.
 * @property hops The list of node identifiers representing the sequence of hops forming the path.
 */
@OptIn(DelicateCollektiveApi::class)
@Serializable
data class PathValue<ID : Any, Value, Distance : Comparable<Distance>>(
    @JvmField
    val distance: Distance,
    @JvmField
    val data: Value,
    @JvmField
    val hops: List<ID> = emptyList(),
) : Comparable<PathValue<ID, Value, Distance>> {
    /**
     * The ID of the original source node where this path begins.
     */
    val source: ID get() = hops.first()

    /**
     * The ID of the neighbor device on the next hop toward this device.
     */
    val nextHop: ID get() = hops.last()

    /**
     * The number of hops (segments) in this path.
     */
    val length: Int get() = hops.size

    /**
     * Returns true if this path includes the specified device ID.
     *
     * @param id The device identifier to check.
     * @return True if the path hops contain the given ID.
     */
    operator fun contains(id: ID): Boolean = hops.contains(id)

    /**
     * Creates a new path by extending this one with a neighbor hop.
     *
     * @param neighbor The ID of the next hop (neighbor device).
     * @param distanceToNeighbor The edge distance to the neighbor.
     * @param bottom The minimum allowed distance (source base).
     * @param top The maximum allowed distance (distance clamp).
     * @param accumulateDistance Reducer to combine two distances and enforce bounds.
     * @param accumulateData Function to update the carried data when crossing the edge.
     * @return A new [PathValue] including the neighbor hop, updated distance, and data.
     */
    @OptIn(DelicateCollektiveApi::class)
    inline fun update(
        neighbor: ID,
        distanceToNeighbor: Distance,
        bottom: Distance,
        top: Distance,
        crossinline accumulateDistance: Reducer<Distance>,
        crossinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value,
    ): PathValue<ID, Value, Distance> {
        val totalDistance = accumulate(bottom, top, distance, distanceToNeighbor, accumulateDistance)
        val updatedData = accumulateData(distance, distanceToNeighbor, data)
        return PathValue(totalDistance, updatedData, hops + neighbor)
    }

    override fun compareTo(other: PathValue<ID, Value, Distance>) =
        compareBy<PathValue<ID, Value, Distance>> { it.distance }.compare(this, other)

    // Check if the path is invalid because there is a shortcut through an intermediate neighboring hop
    @PublishedApi
    internal fun isInvalidViaShortcut(
        accDist: Distance?,
        neighbors: Set<ID>,
        neighborAccumulatedDistances: Map<ID, Distance?>,
    ): Boolean = accDist != null &&
        hops
            .asSequence()
            .filter { it in neighbors }
            .map { neighborAccumulatedDistances[it] }
            .any { it == null || it < accDist }

    // Check if all hops that appear in both paths have the same order
    @PublishedApi
    internal fun allCommonHopsAppearInTheSameOrderOf(
        other: PathValue<ID, Value, Distance>,
        pathsHopSets: Map<ID, Set<ID>>,
    ): Boolean {
        val otherHops = pathsHopSets[other.nextHop].orEmpty()
        val commonHops = hops.filter { it in otherHops }
        return when (commonHops.size) {
            0, 1 -> true
            else -> {
                // otherHops and commonHops must have the same order for all elements
                val commonIterator = commonHops.iterator()
                val otherIterator = otherHops.iterator()
                var matches = 0
                while (commonIterator.hasNext() && otherIterator.hasNext()) {
                    val common = commonIterator.next()
                    val matchesSoFar = matches
                    while (otherIterator.hasNext() && matchesSoFar == matches) {
                        if (common == otherIterator.next()) {
                            matches++
                        }
                    }
                }
                matches == commonHops.size
            }
        }
    }
}

/**
 * Combines two distances with bounds checking and triangle-inequality enforcement.
 *
 * Uses the provided accumulator to sum `distance` and `distanceToNeighbor`, clamps the result
 * between `bottom` and `top`, and verifies the triangle inequality.
 *
 * @param D The comparable type used for distances.
 * @param bottom The minimum allowed distance.
 * @param top The maximum allowed distance.
 * @param distance The current accumulated distance.
 * @param distanceToNeighbor The edge distance to add.
 * @param accumulator Reducer function to combine two distances.
 * @return The new clamped distance result.
 * @throws IllegalStateException if the accumulator violates the triangle inequality.
 */
@OptIn(ExperimentalContracts::class)
@PublishedApi
internal inline fun <D : Comparable<D>> accumulate(
    bottom: D,
    top: D,
    distance: D,
    distanceToNeighbor: D,
    accumulator: Reducer<D>,
): D {
    contract {
        callsInPlace(accumulator, kotlin.contracts.InvocationKind.EXACTLY_ONCE)
    }
    val totalDistance = accumulator(distance, distanceToNeighbor).coerceIn(bottom, top)
    check(totalDistance >= distance && totalDistance >= distanceToNeighbor) {
        "The provided distance accumulation function violates the triangle inequality: " +
            "accumulating $distance and $distanceToNeighbor produced $totalDistance"
    }
    return totalDistance
}
