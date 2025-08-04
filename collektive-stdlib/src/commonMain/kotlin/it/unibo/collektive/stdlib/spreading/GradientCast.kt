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
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.api.exchange
import it.unibo.collektive.aggregate.api.mapNeighborhood
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.toMap
import it.unibo.collektive.aggregate.values
import it.unibo.collektive.stdlib.collapse.valueOfMinBy
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.coerceIn
import it.unibo.collektive.stdlib.util.hops
import it.unibo.collektive.stdlib.util.nonOverflowingPlus
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

/**
 * Computes a gradient-based broadcast of local values over the network using a Bellman–Ford–style algorithm.
 *
 * Starting from the nearest `source` node(s), this function propagates values of type `Value` along a
 * spanning tree defined by a distance metric. At each device:
 * - If `source` is `true`, returns the `local` value at minimal distance `bottom`.
 * - Otherwise, selects the neighbor path yielding the minimal total distance (accumulated via `accumulateDistance`)
 *   and applies `accumulateData` to combine neighbor data.
 * - In the absence of any source or neighbors, returns the `local` value.
 *
 * @param source               `true` if this device is a source; `false` otherwise.
 * @param local                The local value to propagate from a source or to use as a default.
 * @param bottom               The minimum distance (distance at a source). Must be ≤ `top`.
 * @param top                  The maximum distance threshold; values outside `[bottom, top]` are clamped.
 * @param accumulateData       A function to combine data when forwarding from a neighbor.
 *                              Receives the neighbor's distance from source, the edge distance to this device,
 *                              and the neighbor's data. Defaults to the identity function.
 * @param accumulateDistance   A reducer that sums two distances; used to accumulate path lengths.
 * @param metric               A `Field<ID, Distance>` providing the distance to each neighbor.
 *
 * @return The aggregated `Value` at this device, chosen from the minimal-distance path.
 *
 * This function uses *incremental repair* to self-heal the gradient and may suffer from the *rising value problem*.
 * See [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163) for details.
 */
@JvmOverloads
inline fun <reified ID, reified Value, reified Distance> Aggregate<ID>.bellmanFordGradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Distance>,
    metric: Field<ID, Distance>,
): Value where ID : Any, Distance : Comparable<Distance> {
    val topValue: Pair<Distance, Value> = top to local
    val distances = metric.coerceIn(bottom, top)
    return share(topValue) { neighborData ->
        val pathsThroughNeighbors = neighborData.alignedMapValues(distances) { (fromSource, data), toNeighbor ->
            val totalDistance = accumulate(bottom, top, fromSource, toNeighbor, accumulateDistance)
            val newData = accumulateData(fromSource, toNeighbor, data)
            totalDistance to newData
        }
        val bestThroughNeighbors = pathsThroughNeighbors.excludeSelf.valueOfMinBy { it.value.first } ?: topValue
        when {
            source -> bottom to local
            else -> bestThroughNeighbors
        }
    }.second // return the data
}

/**
 * Propagates local values across the network using a Bellman–Ford gradient.
 *
 * Starting from the nearest `source` node(s), this function builds a spanning tree
 * and distributes data of type `Value` along minimal-distance paths. At each device:
 * - If `source` is `true`, returns the `local` value at distance `0.0`.
 * - Otherwise, examines each neighbor’s propagated pair `(distance, data)`, adds the
 *   edge weight via `accumulateDistance`, applies `accumulateData` to the data, and
 *   selects the path with the smallest total distance.
 * - If there are no sources and no neighbors, returns the `local` value.
 *
 * This overload uses a default distance range of `[0.0, +∞)` and the standard
 * addition operator for distance accumulation.
 *
 * @param source             `true` if this device is a source node; `false` otherwise.
 * @param local              The local value to propagate or to use as a default.
 * @param accumulateData     Function to combine neighbor data:
 *                           receives the neighbor’s distance from source,
 *                           the edge distance to this device, and the neighbor’s data.
 *                           Defaults to the identity function.
 * @param accumulateDistance Function to sum two distances; defaults to `Double::plus`.
 * @param metric             A `Field<ID, Double>` providing the edge weight to each neighbor.
 *
 * @return The propagated `Value` at this device, chosen along the shortest-distance path.
 *
 * This function uses *incremental repair* to self-heal the gradient and may suffer
 * from the *rising value problem*. See
 * [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163) for details.
 */
@JvmOverloads
inline fun <reified ID, reified Value> Aggregate<ID>.bellmanFordGradientCast(
    source: Boolean,
    local: Value,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: (fromSource: Double, toNeighbor: Double) -> Double = Double::plus,
    metric: Field<ID, Double>,
): Value where ID : Any =
    bellmanFordGradientCast(source, local, 0.0, Double.POSITIVE_INFINITY, accumulateData, accumulateDistance, metric)

/**
 * Computes a fast, self-healing gradient broadcast of local values from all source nodes,
 * always retaining the data from the nearest source.
 *
 * Starting from every device where `source` is `true`, this function propagates data of type `Value`
 * along multiple overlapping spanning trees. At each device:
 * - If `source` is `true`, initializes its own `local` value at distance `bottom`.
 * - Otherwise, gathers neighbor-propagated `GradientPath<ID, Value, Distance>?` values,
 *   filters out nulls, loops (via `maxDiameter` and hop sets), and paths invalidated by
 *   inconsistent neighbor distances, then selects the minimal-distance, path-coherent route.
 * - Uses `accumulateDistance` to sum edge distances and `accumulateData` to update the data payload.
 * - Falls back to `local` if no valid source path exists.
 *
 * This algorithm employs *fast repair* and is **not** subject to the rising-value problem,
 * but it incurs larger message sizes and more computation than [bellmanFordGradientCast].
 * To limit work and prevent infinite loops, supply an estimate of the network diameter via `maxDiameter`.
 *
 * @param source             `true` if this device is a source node; otherwise `false`.
 * @param local              The local data value for this device, or the default if no source path exists.
 * @param bottom             The zero-distance value at a source. Must be ≤ `top`.
 * @param top                The maximum allowed distance; incoming distances are clamped into `[bottom, top]`.
 * @param metric             A `Field<ID, Distance>` providing edge weights to each neighbor.
 * @param maxDiameter        Upper bound on path hop count; paths longer than this are discarded.
 *                          Defaults to `Int.MAX_VALUE`.
 * @param accumulateData     Function to update neighbor data when traversing an edge.
 *                          Receives the neighbor’s distance from source, the edge distance,
 *                          and the neighbor’s data, returning a new `Value`. Defaults to identity.
 * @param accumulateDistance A reducer to sum two `Distance` values; used for accumulating path lengths.
 * @param ID       The type used for neighbor identifiers.
 * @param Value    The type of data being propagated.
 * @param Distance The type representing path lengths; must be comparable.
 *
 * @return The `Value` associated with the nearest source node, following a minimal-distance, loop-free path,
 *         or `local` if no source paths are found.
 *
 * @see bellmanFordGradientCast
 * @see <a href="https://doi.org/10.1145/1363686.1364163">Fast self-healing gradients</a>
 */
@OptIn(DelicateCollektiveApi::class)
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, neighborData: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Distance>,
): Value {
    val coercedMetric = metric.coerceIn(bottom, top)
    val fromLocalSource = if (source) GradientPath(bottom, emptyList<ID>(), local) else null
    return exchange(fromLocalSource) { neighborData: Field<ID, GradientPath<ID, Value, Distance>?> ->
        val neighbors = neighborData.neighbors
        // Accumulated distances with neighbors, to be used to exclude invalid paths
        val accDistances = neighborData.alignedMapValues(coercedMetric) { path, distance ->
            path?.distance?.let { accumulateDistance(it, distance) }
        }
        val neighborAccumulatedDistances = accDistances.excludeSelf.toMap()
        val nonLoopingPaths = neighborData.alignedMap(accDistances, coercedMetric) { id, path, accDist, distance ->
            path?.takeUnless { id == localId }
                ?.takeUnless { path.length > maxDiameter }
                ?.takeUnless { localId in path.hops }
                ?.takeUnless { path.isInvalidViaShortcut(accDist, neighbors, neighborAccumulatedDistances) }
                ?.run { accDist to lazy { update(id, distance, bottom, top, accumulateDistance, accumulateData) } }
        }.excludeSelf.values().sequence.filterNotNull().sortedBy { it.first }.map { it.second.value }
        val best = when {
            fromLocalSource != null -> sequenceOf(fromLocalSource)
            else -> {
                val pathsHopSets by lazy { nonLoopingPaths.associate { it.nextHop to it.hops.toSet() } }
                nonLoopingPaths.filter { reference ->
                    /*
                     * Path-coherence: paths that contain inconsistent information must be removed.
                     * In particular, if some path passes through A and then B, and another reaches the source
                     * through B and then A, we must keep only the shortest
                     * (unless they have the same path-length, namely the network is symmetric).
                     */
                    val refSize = reference.length
                    refSize <= 1 ||
                        nonLoopingPaths.all { other ->
                            other.length > refSize ||
                                // the current reference is shorter
                                // same hop count, same distance (symmetric network or same path)
                                (other.length == refSize && other.distance == reference.distance) ||
                                // all common hops appear in the same order
                                reference.allCommonHopsAppearInTheSameOrderOf(other, pathsHopSets)
                        }
                }
            }
        }
        val bestLazyList = best.map { lazy { it } }.toList()
        mapNeighborhood { neighbor -> bestLazyList.firstOrNull { it.value.hops.lastOrNull() != neighbor }?.value }
    }.local.value?.data ?: local
}

/**
 * Broadcasts a fast-repair gradient of local values from all source nodes,
 * always retaining the data from the nearest source.
 *
 * This overload delegates to the full `gradientCast(...)` implementation using
 * a default distance range of `[0.0, +∞)` and the standard addition reducer.
 *
 * @param source `true` if this device is a source node; otherwise `false`.
 * @param local The local data value or the fallback if no source path exists.
 * @param metric A `Field<ID, Double>` that provides the edge weight (distance) to each neighbor.
 * @param maxDiameter Upper limit on path hop count; any path exceeding this is discarded.
 *    Defaults to `Int.MAX_VALUE`.
 * @param accumulateData Function to update neighbor data when traversing an edge.
 *    Receives the neighbor’s distance from source, the edge distance,
 *    and the neighbor’s data, returning a new data value.
 *    Defaults to the identity function.
 * @param accumulateDistance Function to combine two distances; defaults to `Double::plus`.
 *
 * @return The data associated with the nearest source node, following a minimal-distance,
 *         loop-free path, or `local` if no source is reachable.
 *
 * @see gradientCast
 * @see bellmanFordGradientCast
 * @see <a href="https://doi.org/10.1145/1363686.1364163">Fast self-healing gradients</a>
 */
@JvmOverloads
inline fun <reified ID : Any, reified Type> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Type,
    metric: Field<ID, Double>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Type) -> Type = { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Double> = Double::plus,
): Type = gradientCast(
    source,
    local,
    0.0,
    Double.POSITIVE_INFINITY,
    metric,
    maxDiameter,
    accumulateData,
    accumulateDistance,
)

/**
 * Broadcasts a fast-repair integer gradient of local values from all source nodes,
 * always retaining the data from the nearest source.
 *
 * This overload applies when distances are `Int`, using a default range of `[0, Int.MAX_VALUE]`
 * and the standard non-overflowing addition reducer.
 *
 * At each device:
 * - If `source` is `true`, propagates its own `local` value with distance `0`.
 * - Otherwise, collects neighbor paths, accumulates distances via `accumulateDistance`,
 *   applies `accumulateData` to the neighbor’s data, and selects the path with minimal total distance.
 * - If no source path exists, returns `local`.
 *
 * @param ID The type used for neighbor identifiers.
 * @param Type The type of data being propagated.
 * @param source `true` if this device is a source node; otherwise `false`.
 * @param local The local data value or fallback if no source is reachable.
 * @param metric A `Field<ID, Int>` providing edge weights to each neighbor.
 * @param maxDiameter Maximum allowed number of hops; paths longer than this are discarded. Defaults to `Int.MAX_VALUE`.
 * @param accumulateData Function to update neighbor data on each hop: receives the neighbor’s distance from source,
 * the edge distance, and the neighbor’s data. Defaults to the identity function.
 * @param accumulateDistance Reducer to combine two distances; defaults to `Int::nonOverflowingPlus`.
 *
 * @return The data associated with the nearest source node along a minimal-distance,
 * loop-free path, or `local` if no source is reachable.
 *
 * This algorithm employs *fast repair* and is **not** subject to the *rising value problem*.
 * It requires larger messages and more computation than [bellmanFordGradientCast].
 * To improve performance, provide an upper bound on the network diameter via `maxDiameter`.
 *
 * @see bellmanFordGradientCast
 * @see <a href="https://doi.org/10.1145/1363686.1364163">Fast self-healing gradients</a>
 */
@JvmOverloads
inline fun <reified ID : Any, reified Type> Aggregate<ID>.intGradientCast(
    source: Boolean,
    local: Type,
    metric: Field<ID, Int>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Int> = Int::nonOverflowingPlus,
): Type = gradientCast(
    source,
    local,
    0,
    Int.MAX_VALUE,
    metric,
    maxDiameter,
    accumulateData,
    accumulateDistance,
)

/**
 * Broadcasts a fast-repair gradient of local values using hop count as the distance metric.
 *
 * Propagates values from every device where `source` is true, retaining the data from
 * the nearest source (fewest hops). Internally delegates to [intGradientCast] with
 * `hops()` as the metric and `Int::plus` as the distance reducer.
 *
 * @param ID The type representing neighbor identifiers.
 * @param Type The type of data being propagated.
 * @param source True if this device is a source node; false otherwise.
 * @param local The local data value or fallback if no source is reachable.
 * @param maxDiameter Upper bound on path hop count; paths longer than this are discarded. Defaults to Int.MAX_VALUE.
 * @param accumulateData Function to update neighbor data on each hop. Receives the neighbor’s
 *                       distance from source (hop count), the hop increment (always 1),
 *                       and the neighbor’s data. Defaults to the identity function.
 * @return The data associated with the nearest source node along a minimal-hop,
 *         loop-free path, or `local` if no source is reachable.
 *
 * @see intGradientCast
 * @see bellmanFordGradientCast
 * @see <a href="https://doi.org/10.1145/1363686.1364163">Fast self-healing gradients</a>
 */
@JvmOverloads
inline fun <reified ID : Any, reified Type> Aggregate<ID>.hopGradientCast(
    source: Boolean,
    local: Type,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
): Type = intGradientCast(
    source,
    local,
    hops(),
    maxDiameter,
    accumulateData,
    Int::plus,
)

/**
 * Computes, for each ID in `sources`, a fast-repair gradient propagation of `local` values,
 * returning a map from each source ID to its propagated value at this device.
 *
 * For each `source` in `sources`, this function invokes [gradientCast] with that ID as the sole source.
 * If no valid path exists from a given source, the `local` value is used as a fallback.
 *
 * @param ID The type used for neighbor identifiers.
 * @param Value The type of data being propagated.
 * @param Distance The comparable type representing distances.
 * @param sources The collection of device IDs to treat as sources.
 * @param local The local data value or default if a source is unreachable.
 * @param bottom The zero-distance value at a source. Must be ≤ `top`.
 * @param top The maximum distance threshold; incoming distances are clamped into `[bottom, top]`.
 * @param metric A `Field<ID, Distance>` providing edge weights to each neighbor.
 * @param maxDiameter Maximum allowed number of hops; paths longer than this are discarded. Defaults to `Int.MAX_VALUE`.
 * @param accumulateData Function to combine neighbor data on each hop: receives the neighbor’s distance from source,
 * the edge distance, and the neighbor’s data. Defaults to the identity function.
 * @param accumulateDistance Reducer to accumulate two `Distance` values; used for path-length accumulation.
 *
 * @return A `Map<ID, Value>` mapping each source ID to the propagated data value at this device.
 *
 * @see gradientCast
 */
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Distance>,
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(
            source == localId,
            local,
            bottom,
            top,
            metric,
            maxDiameter,
            accumulateData,
            accumulateDistance,
        )
    }
}

/**
 * Computes a fast-repair gradient propagation from multiple sources,
 * returning a map of the propagated values for each source ID.
 *
 * For each ID in `sources`, this function invokes [gradientCast] treating only that ID
 * as the source node. It uses `metric` to compute edge distances, applies `accumulateData`
 * at each hop, and accumulates distances by plain addition (`Double::plus`). If a source
 * is unreachable or there are no neighbors, it falls back to `local`.
 *
 * @param ID The type used for device identifiers.
 * @param Value The type of data being propagated.
 * @param sources The collection of device IDs to use as sources.
 * @param local The local data value or default if a source path is not found.
 * @param metric A `Field<ID, Double>` providing edge weights to each neighbor.
 * @param maxDiameter Maximum allowed hops before discarding a path. Defaults to `Int.MAX_VALUE`.
 * @param accumulateData Function to update data on each hop. Receives the neighbor’s distance from the source,
 * the edge distance to this device, and the neighbor’s data. Defaults to the identity function.
 *
 * @return A `Map<ID, Value>` mapping each source ID to its propagated value at this device.
 *
 * @see gradientCast
 */
@JvmOverloads
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    metric: Field<ID, Double>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Value) -> Value = { _, _, data -> data },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(
            source = source == localId,
            local = local,
            metric = metric,
            maxDiameter = maxDiameter,
            accumulateData,
            Double::plus,
        )
    }
}

/**
 * For each ID in `sources`, propagates data from that source using a fast-repair integer gradient,
 * and collects the results in a map from source ID to propagated value.
 *
 * Delegates to [intGradientCast], which uses a default distance of `0` to `Int.MAX_VALUE`,
 * the hop-count metric (`hops()`), and non-overflowing addition for distance accumulation.
 * If a source is unreachable or there are no neighbors, the `local` value is used.
 *
 * @param ID The type used for device identifiers.
 * @param Value The type of data being propagated.
 * @param sources Collection of device IDs to use as sources.
 * @param local The local data value or default if a source path is not found.
 * @param metric A `Field<ID, Int>` providing the hop-count metric to each neighbor. Defaults to `hops()`.
 * @param maxDiameter Maximum allowed hops before discarding a path. Defaults to `Int.MAX_VALUE`.
 * @param accumulateData Function to update neighbor data on each hop. Receives the neighbor’s
 * distance from the source, the hop increment (always 1),
 * and the neighbor’s data. Defaults to the identity function.
 *
 * @return A `Map<ID, Value>` mapping each source ID to its propagated value at this device.
 *
 * @see intGradientCast
 */
@JvmOverloads
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiIntGradientCast(
    sources: Iterable<ID>,
    local: Value,
    metric: Field<ID, Int> = hops(),
    maxDiameter: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Value) -> Value = { _, _, data -> data },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        intGradientCast(
            source = source == localId,
            local = local,
            metric = metric,
            maxDiameter = maxDiameter,
            accumulateData = accumulateData,
            accumulateDistance = Int::nonOverflowingPlus,
        )
    }
}

/**
 * A path segment representing a route from a source device to the current device.
 *
 * Each segment holds the total distance traveled, the list of hops (device IDs) along the path,
 * and the data carried from the source at each step.
 *
 * This data class is used internally by [gradientCast] and related functions to track
 * path metadata and propagated values.
 *
 * @param ID The type used for device identifiers in the path.
 * @param Value The type of data being propagated along the path.
 * @param Distance The comparable type representing path lengths.
 * @param distance The total accumulated distance from the source to this device.
 * @param hops The sequence of device IDs, starting with the source and ending with the current device.
 * @param data The value propagated along this path.
 */
@Serializable
@PublishedApi
internal data class GradientPath<ID : Any, Value, Distance : Comparable<Distance>>(
    val distance: Distance,
    val hops: List<ID>,
    val data: Value,
) : Comparable<GradientPath<ID, Value, Distance>> {

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
     * @return A new [GradientPath] including the neighbor hop, updated distance, and data.
     */
    @OptIn(DelicateCollektiveApi::class)
    inline fun update(
        neighbor: ID,
        distanceToNeighbor: Distance,
        bottom: Distance,
        top: Distance,
        crossinline accumulateDistance: Reducer<Distance>,
        crossinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value,
    ): GradientPath<ID, Value, Distance> {
        val totalDistance = accumulate(bottom, top, distance, distanceToNeighbor, accumulateDistance)
        val updatedData = accumulateData(distance, distanceToNeighbor, data)
        return GradientPath(totalDistance, hops + neighbor, updatedData)
    }

    override fun compareTo(other: GradientPath<ID, Value, Distance>) =
        compareBy<GradientPath<ID, Value, Distance>> { it.distance }.compare(this, other)

    // Check if the path is invalid because there is a shortcut through an intermediate neighboring hop
    @PublishedApi
    internal fun isInvalidViaShortcut(
        accDist: Distance?,
        neighbors: Set<ID>,
        neighborAccumulatedDistances: Map<ID, Distance?>,
    ): Boolean = accDist != null &&
        hops.asSequence()
            .filter { it in neighbors }
            .map { neighborAccumulatedDistances[it] }
            .any { it == null || it < accDist }

    // Check if all hops that appear in both paths have the same order
    @PublishedApi
    internal fun allCommonHopsAppearInTheSameOrderOf(
        other: GradientPath<ID, Value, Distance>,
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
