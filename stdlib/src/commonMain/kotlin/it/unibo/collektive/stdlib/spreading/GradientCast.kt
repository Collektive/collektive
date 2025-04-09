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
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.operations.minBy
import it.unibo.collektive.stdlib.util.Accumulator
import it.unibo.collektive.stdlib.util.coerceIn
import it.unibo.collektive.stdlib.util.hops
import it.unibo.collektive.stdlib.util.nonOverflowingPlus
import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.jvm.JvmOverloads

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
@JvmOverloads
inline fun <reified ID, reified Value, reified Distance> Aggregate<ID>.bellmanFordGradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Accumulator<Distance>,
    metric: Field<ID, Distance>,
): Value where ID : Any, Distance : Comparable<Distance> {
    val topValue = top to local
    val distances = metric.coerceIn(bottom, top)
    return share(topValue) { neighborData ->
        val paths = neighborData.alignedMap(distances) { (fromSource, data), toNeighbor ->
            val totalDistance = accumulateDistance(fromSource, toNeighbor).coerceIn(bottom, top)
            check(totalDistance >= fromSource && totalDistance >= toNeighbor) {
                "The provided distance accumulation function violates the triangle inequality: " +
                    "accumulating $fromSource and $toNeighbor produced $totalDistance"
            }
            val newData = accumulateData(fromSource, toNeighbor, data)
            totalDistance to newData
        }
        when {
            source -> bottom to local
            else -> paths.minBy(base = topValue) { it.first } // sort by distance from the nearest source
        }
    }.second // return the data
}

/**
 * Propagate [local] values across a spanning tree starting from the closest [source].
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Double]s,
 * [accumulateDistance] is used to sum distances.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 *
 * This function features *incremental repair*, and it is subject to the *rising value problem*,
 * see [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163).
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
 * Propagate [local] values across multiple spanning trees starting from all the devices in which [source] holds,
 * retaining the value of the closest source.
 *
 * If there are no sources, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Distance]s.
 * [Distance]s must be in the [[bottom], [top]] range, [accumulateDistance] is used to sum distances.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 *
 * This function features *fast repair*, and it is **not** subject to the *rising value problem*,
 * see [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163).
 *
 * On the other hand, it requires larger messages and more processing than the classic
 * [bellmanFordGradientCast].
 */
@OptIn(DelicateCollektiveApi::class, ExperimentalContracts::class)
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, neighborData: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Accumulator<Distance>,
): Value {
    require(maxPaths > 0) {
        "Computing the gradient requires at least one-path memory"
    }
    val coercedMetric = metric.coerceIn(bottom, top)
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
        ) { accumulator, paths ->
            paths.forEach { path ->
                val key = path.source to path.neighbor
                val previous = accumulator.getOrPut(key) { path }
                if (previous.totalDistance > path.totalDistance) {
                    accumulator[key] = path
                }
            }
            accumulator
        }.values.sorted()
        /*
         * Keep at most maxPaths paths, unless it is a source (in which case, it is maxPaths -1).
         */
        val toTake = maxPaths - fromLocalSource.size
        val sharedPaths = fromLocalSource + when {
            // We can keep all paths
            candidatePaths.size <= toTake ->
                candidatePaths.map { it.toLocalPath(accumulateData) }
            else -> {
                ArrayList<GradientPath<ID, Value, Distance>>(maxPaths).apply {
                    /*
                     * Pick the paths by priority:
                     * 1. prepare a list of results, large at most multi-paths;
                     * 2. classify them by source in a Map<ID, List<UpdatedGradientPath>>;
                     * 3. pick the shortest for each source, removing it from the list;
                     * 4. sort the selected ones by distance, and pick at most multi-paths of them.
                     * 5. if there are more slots, goto 2.
                     */
                    val pathsBySource = mutableMapOf<ID, MutableList<UpdatedGradientPath<ID, Value, Distance>>>()
                    // Equivalent to a groupBy, but presumably faster with large networks
                    candidatePaths.forEach {
                        // Since the source collection is sorted, the subcollections will be sorted as well
                        pathsBySource.getOrPut(it.path.source) { mutableListOf() }.apply { add(it) }
                    }
                    when {
                        pathsBySource.size == 1 -> {
                            // If there is only one source, take as many paths as necessary from there
                            val toAdd = pathsBySource.values.single()
                                .asSequence()
                                .take(toTake)
                                .map { it.toLocalPath(accumulateData) }
                            addAll(toAdd)
                        }
                        else -> fillAlternated(toTake, pathsBySource.values) { toLocalPath(accumulateData) }
                    }
                }
            }
        }
        sharedPaths.yielding { sharedPaths.firstOrNull()?.data ?: local }
    }
}

/**
 * Fills [this] list with alternate elements from [sources] until it reaches [toTake] elements,
 * transforming each one with [transform].
 */
@PublishedApi
internal inline fun <Initial, Transformed> ArrayList<Transformed>.fillAlternated(
    toTake: Int,
    sources: MutableCollection<MutableList<Initial>>,
    crossinline transform: Initial.() -> Transformed,
) {
    while (size < toTake) {
        val toIterate = sources.iterator()
        while (toIterate.hasNext() && size < toTake) {
            val pathsForSource = toIterate.next()
            val path = pathsForSource.removeFirst()
            if (pathsForSource.isEmpty()) {
                toIterate.remove()
            }
            add(path.transform())
        }
    }
}

/**
 * Propagate [local] values across multiple spanning trees starting from all the devices in which [source] holds,
 * retaining the value of the closest source.
 *
 * If there are no sources, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Double]s,
 * [accumulateDistance] is used to accumulate distances, defaulting to a plain sum.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 *
 * This function features *fast repair*, and it is **not** subject to the *rising value problem*,
 * see [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163).
 *
 * On the other hand, it requires larger messages and more processing than the classic
 * [bellmanFordGradientCast].
 */
@JvmOverloads
inline fun <reified ID : Any, reified Type> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Type,
    metric: Field<ID, Double>,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Type) -> Type = { _, _, data -> data },
    crossinline accumulateDistance: Accumulator<Double> = Double::plus,
): Type = gradientCast(
    source,
    local,
    0.0,
    Double.POSITIVE_INFINITY,
    metric,
    maxPaths,
    accumulateData,
    accumulateDistance,
)

/**
 * Propagate [local] values across multiple spanning trees starting from all the devices in which [source] holds,
 * retaining the value of the closest source.
 *
 * If there are no sources, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Int]s,
 * [accumulateDistance] is used to accumulate distances, defaulting to a plain sum.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 *
 * This function features *fast repair*, and it is **not** subject to the *rising value problem*,
 * see [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163).
 *
 * On the other hand, it requires larger messages and more processing than the classic
 * [bellmanFordGradientCast].
 */
@JvmOverloads
inline fun <reified ID : Any, reified Type> Aggregate<ID>.intGradientCast(
    source: Boolean,
    local: Type,
    metric: Field<ID, Int>,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
    crossinline accumulateDistance: Accumulator<Int> = Int::nonOverflowingPlus,
): Type = gradientCast(source, local, 0, Int.MAX_VALUE, metric, maxPaths, accumulateData, accumulateDistance)

/**
 * Propagate [local] values across multiple spanning trees starting from all the devices in which [source] holds,
 * retaining the value of the closest source, using the hop count as distance metric.
 *
 * If there are no sources, default to [local] value.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 *
 * This function features *fast repair*, and it is **not** subject to the *rising value problem*,
 * see [Fast self-healing gradients](https://doi.org/10.1145/1363686.1364163).
 *
 * On the other hand, it requires larger messages and more processing than the classic
 * [bellmanFordGradientCast].
 */
@JvmOverloads
inline fun <reified ID : Any, reified Type> Aggregate<ID>.hopGradientCast(
    source: Boolean,
    local: Type,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
): Type = intGradientCast(source, local, hops(), maxPaths, accumulateData, Int::plus)

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
    metric: Field<ID, Distance>,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Accumulator<Distance>,
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(source == localId, local, bottom, top, metric, maxPaths, accumulateData, accumulateDistance)
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
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    metric: Field<ID, Double>,
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Value) -> Value = { _, _, data -> data },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(
            source = source == localId,
            local = local,
            metric = metric,
            maxPaths = maxPaths,
            accumulateData,
            Double::plus,
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
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiIntGradientCast(
    sources: Iterable<ID>,
    local: Value,
    metric: Field<ID, Int> = hops(),
    maxPaths: Int = Int.MAX_VALUE,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Value) -> Value = { _, _, data -> data },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        intGradientCast(
            source = source == localId,
            local = local,
            metric = metric,
            maxPaths = maxPaths,
            accumulateData = accumulateData,
            accumulateDistance = Int::nonOverflowingPlus,
        )
    }
}

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
 * and it is not intended to be used outside it.
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
