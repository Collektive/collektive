/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.stdlib.util.coerceIn
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmName
import kotlin.jvm.JvmOverloads

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
 * A path segment along a potential field that reaches the current device,
 * after [distance], starting from [source],
 * passing [through] an intermediate direct neighbor,
 * carrying [data].
 *
 * This data class is designed to be shared within [gradientCast] and derivative functions.
 */
@Serializable
data class GradientPath<ID : Any, Value, Distance : Comparable<Distance>>(
    val distance: Distance,
    val through: ID,
    val source: ID,
    val data: Value,
)

/**
 * A two-segment path along a potential field that reaches the current device,
 * after [totalDistance], starting from [source],
 * passing through an intermediate direct [neighbor]
 * distant [distanceToNeighbor] from the current device,
 * to which it arrives through [path].
 *
 * This class is meant to be used internally by the [gradientCast] function,
 * and it is not intended to be used outside of it.
 */
@DelicateCollektiveApi
data class UpdatedGradientPath<ID : Any, Value, Distance : Comparable<Distance>>(
    val neighbor: ID,
    val totalDistance: Distance,
    val distanceToNeighbor: Distance,
    val path: GradientPath<ID, Value, Distance>,
) : Comparable<UpdatedGradientPath<ID, Value, Distance>> {

    val source = path.source

    override fun compareTo(other: UpdatedGradientPath<ID, Value, Distance>): Int =
        totalDistance.compareTo(other.totalDistance)

    /**
     * Convert this path to a [GradientPath] by accumulating the data from the source to the neighbor.
     *
     * @param accumulateData function to accumulate data from the source to the neighbor.
     */
    fun toLocalPath(
        accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value,
    ): GradientPath<ID, Value, Distance> =
        GradientPath(totalDistance, neighbor, source, accumulateData(path.distance, distanceToNeighbor, path.data))
}

/**
 * Propagate [local] values across a spanning tree starting from the closest [source].
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Distance]s.
 * [Distance]s must be in the [[bottom], [top]] range, [accumulateDistance] is used to sum distances.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 *
 * This function features *incremental repair*, and it is subject to the *rising value problem*,
 * see [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163).
 */
@OptIn(DelicateCollektiveApi::class)
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: (fromSource: Distance, toNeighbor: Distance) -> Distance,
    crossinline metric: () -> Field<ID, Distance>,
): Value {
    require(maxPaths > 0) {
        "Computing the gradient requires at least one-path memory"
    }
    val fromLocalSource = if (source) listOf(GradientPath(bottom, localId, localId, local)) else emptyList()
    return sharing(fromLocalSource) { neighborData: Field<ID, List<GradientPath<ID, Value, Distance>>> ->
        /*
         * Multi-path gradient repair: share a set of paths, invalidate those for which the last step contains
         * this node or any neighbor.
         * Respecting the max size (maxPaths), find the shortest path towards each source
         * and alternative paths towards the same source.
         */
        val neighbors = neighborData.neighbors
        val validPaths =
            neighborData.mapWithId { neighbor: ID, paths ->
                paths.filter { (_, neighborsNeighbor: ID, source: ID, _) ->
                    neighbor == source || neighborsNeighbor != localId && neighborsNeighbor !in neighbors
                }
            }
        val coercedMetric = metric().coerceIn(bottom, top)
        val distanceUpdatedPaths = validPaths.alignedMapWithId(coercedMetric) { id, paths, distanceToNeighbor ->
            paths.map { path ->
                val totalDistance = accumulateDistance(path.distance, distanceToNeighbor).coerceIn(bottom, top)
                check(totalDistance >= path.distance) {
                    "The provided distance accumulation function violates the triangle inequality: " +
                        "accumulating ${path.distance} and $distanceToNeighbor produced $totalDistance"
                }
                UpdatedGradientPath(id, totalDistance, distanceToNeighbor, path)
            }
        }
        /*
         * Take one path per source and neighbor (the one with the shortest distance).
         */
        val candidatePaths = distanceUpdatedPaths.fold(
            mutableMapOf<Pair<ID, ID>, UpdatedGradientPath<ID, Value, Distance>>(),
        ) { list, paths ->
            paths.forEach { path ->
                val key = path.source to path.neighbor
                val previous = list.getOrPut(key) { path }
                if (previous.totalDistance > path.totalDistance) {
                    list[key] = path
                }
            }
            list
        }.values.sorted()
        /*
         * Keep at most maxPaths paths, unless it is source (in which case, it is maxPaths -1).
         */
        val sharedPaths = fromLocalSource + when {
            // We can keep all paths
            candidatePaths.size + fromLocalSource.size <= maxPaths ->
                candidatePaths.map { it.toLocalPath(accumulateData) }
            else -> {
                ArrayList<GradientPath<ID, Value, Distance>>(maxPaths).apply {
                    /*
                     * Pick the paths by priority:
                     * 1. prepare a list of results, large at most multipaths;
                     * 2. classify them by source in a Map<ID, List<UpdatedGradientPath>>;
                     * 3. pick the shortest for each source, removing it from the list;
                     * 4. sort the selected ones by distance, and pick at most multipaths of them.
                     * 5. if there are more slots, goto 2.
                     */
                    val pathsBySource = mutableMapOf<ID, MutableList<UpdatedGradientPath<ID, Value, Distance>>>()
                    // Equivalent to a groupBy, but presumably faster with large networks
                    candidatePaths.forEach {
                        // Since the source collection is sorted, the sub-collections will be sorted as well
                        pathsBySource.getOrPut(it.path.source) { mutableListOf() }.apply { add(it) }
                    }
                    while (size < maxPaths) {
                        val candidates = pathsBySource.values
                            .mapNotNull { it.removeFirstOrNull() }
                            .take(maxPaths - size - fromLocalSource.size)
                            .map { it.toLocalPath(accumulateData) }
                        addAll(candidates)
                    }
                }
            }
        }
        sharedPaths.yielding { sharedPaths.firstOrNull()?.data ?: local }
    }
}

/**
 * Propagate [local] values across a spanning tree starting from the closest [source].
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Int]s.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 */
@JvmOverloads
@JvmName("gradientCastInt")
inline fun <reified ID : Any, reified Type> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Type,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
    crossinline metric: () -> Field<ID, Int> = { neighboring(1) },
): Type = gradientCast(source, local, Int.MIN_VALUE, Int.MAX_VALUE, maxPaths, accumulateData, Int::plus, metric)

/**
 * Propagate [local] values across a spanning tree starting from the closest [source].
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Double]s.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 */
@JvmOverloads
@JvmName("gradientCastDouble")
inline fun <reified ID : Any, reified Type> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Type,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Type) -> Type = { _, _, data -> data },
    crossinline metric: () -> Field<ID, Double> = { neighboring(1.0) },
): Type = gradientCast(source, local, 0.0, Double.POSITIVE_INFINITY, maxPaths, accumulateData, Double::plus, metric)

/**
 * Provided a list of [sources], propagates information from each, collecting it in a map.
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Float]s.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 */
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    bottom: Distance,
    top: Distance,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: (Distance, Distance) -> Distance,
    crossinline metric: () -> Field<ID, Distance>,
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(source == localId, local, bottom, top, maxPaths, accumulateData, accumulateDistance, metric)
    }
}

/**
 * Provided a list of [sources], propagates information from each, collecting it in a map.
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Float]s.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 */
@JvmOverloads
@JvmName("multiGradientCastDouble")
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Value) -> Value = { _, _, data -> data },
    crossinline metric: () -> Field<ID, Double> = { neighboring(1.0) },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(
            source = source == localId,
            local = local,
            bottom = 0.0,
            top = Double.POSITIVE_INFINITY,
            maxPaths = maxPaths,
            accumulateData,
            Double::plus,
            metric,
        )
    }
}

/**
 * Provided a list of [sources], propagates information from each, collecting it in a map.
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Float]s.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 */
@JvmOverloads
@JvmName("multiGradientCastInt")
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Value) -> Value = { _, _, data -> data },
    crossinline metric: () -> Field<ID, Int> = { neighboring(1) },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(
            source = source == localId,
            local = local,
            bottom = Int.MIN_VALUE,
            top = Int.MAX_VALUE,
            maxPaths = maxPaths,
            accumulateData = accumulateData,
            accumulateDistance = Int::plus,
            metric = metric,
        )
    }
}
