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
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.aggregate.api.sharing
import it.unibo.collektive.stdlib.fields.foldValues
import it.unibo.collektive.stdlib.fields.minValueBy
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.coerceIn
import it.unibo.collektive.stdlib.util.hops
import it.unibo.collektive.stdlib.util.nonOverflowingPlus
import kotlinx.serialization.Serializable
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
    crossinline accumulateDistance: Reducer<Distance>,
    metric: Field<ID, Distance>,
): Value where ID : Any, Distance : Comparable<Distance> {
    val topValue: Pair<Distance, Value> = top to local
    val distances = metric.coerceIn(bottom, top)
    return share(topValue) { neighborData ->
        val paths = neighborData.alignedMapValues(distances) { (fromSource, data), toNeighbor ->
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
            else ->
                paths.minValueBy { it.value.first } ?: topValue // sort by distance from the nearest source
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
@OptIn(DelicateCollektiveApi::class)
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxPaths: Int = Int.MAX_VALUE,
    isRiemannianManifold: Boolean = true,
    noinline accumulateData: (fromSource: Distance, toNeighbor: Distance, neighborData: Value) -> Value =
        { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Distance>,
): Value {
    require(maxPaths > 0) {
        "Computing the gradient requires at least one-path memory"
    }
    val coercedMetric = metric.coerceIn(bottom, top)
    val fromLocalSource = if (source) listOf(GradientPath(bottom, localId, localId, local)) else emptyList()
    return sharing(fromLocalSource) { neighborData: Field<ID, List<GradientPath<ID, Value, Distance>>> ->
        val distanceUpdatedPaths = neighborData.alignedMap(coercedMetric) { neighbor, paths, distanceToNeighbor ->
            paths.mapNotNull { path ->
                when {
                    // Previous data, discarded anyway when reducing, or loopback to self
                    neighbor == localId || path.through == localId -> null
                    /*
                     * In Riemannian manifolds, the distance is always positive and the triangle inequality holds.
                     * Thus, we can safely discard paths that pass through a direct neighbor
                     * (except for neighbors that are sources),
                     * as the distance will be always larger than getting to the neighbor directly.
                     */
                    isRiemannianManifold && !path.comesFromSource && path.through in neighborData.neighbors -> null
                    // Keep the path
                    else -> path.updateDistance(neighbor, distanceToNeighbor, bottom, top, accumulateDistance)
                }
            }
        }
        /*
         * Take one path per source and neighbor (the one with the shortest distance).
         */
        val candidatePaths = distanceUpdatedPaths.foldValues(
            mutableMapOf<ID, UpdatedGradientPath<ID, Value, Distance>>(),
        ) { accumulator, paths ->
            paths.forEach { path ->
                val key = path.source
                val previous = accumulator.getOrPut(key) { path }
                if (previous.totalDistance > path.totalDistance) {
                    accumulator[key] = path
                }
            }
            accumulator
        }.values.sorted()
        /*
         * Keep at most maxPaths paths, including the local source.
         */
        val topCandidates = candidatePaths.asSequence()
            .take(maxPaths - fromLocalSource.size)
            .map { it.toLocalPath(accumulateData) }
        val shared = fromLocalSource + topCandidates
        check(shared.size <= maxPaths) {
            "Bug in gradientCast: the number of paths exceeds the maximum allowed: ${shared.size} > $maxPaths."
        }
        shared.yielding { firstOrNull()?.data ?: local }
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
    isRiemannianManifold: Boolean = true,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Type) -> Type = { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Double> = Double::plus,
): Type = gradientCast(
    source,
    local,
    0.0,
    Double.POSITIVE_INFINITY,
    metric,
    maxPaths,
    isRiemannianManifold,
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
    isRiemannianManifold: Boolean = true,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
    crossinline accumulateDistance: Reducer<Int> = Int::nonOverflowingPlus,
): Type = gradientCast(
    source,
    local,
    0,
    Int.MAX_VALUE,
    metric,
    maxPaths,
    isRiemannianManifold,
    accumulateData,
    accumulateDistance,
)

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
): Type = intGradientCast(source, local, hops(), maxPaths, true, accumulateData, Int::plus)

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
    isRiemannianManifold: Boolean = true,
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
            maxPaths,
            isRiemannianManifold,
            accumulateData,
            accumulateDistance,
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
inline fun <reified ID : Any, reified Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    metric: Field<ID, Double>,
    maxPaths: Int = Int.MAX_VALUE,
    isRiemannianManifold: Boolean = true,
    noinline accumulateData: (fromSource: Double, toNeighbor: Double, data: Value) -> Value = { _, _, data -> data },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        gradientCast(
            source = source == localId,
            local = local,
            metric = metric,
            maxPaths = maxPaths,
            isRiemannianManifold = isRiemannianManifold,
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
    isRiemannianManifold: Boolean = true,
    noinline accumulateData: (fromSource: Int, toNeighbor: Int, data: Value) -> Value = { _, _, data -> data },
): Map<ID, Value> = sources.associateWith { source ->
    alignedOn(source) {
        intGradientCast(
            source = source == localId,
            local = local,
            metric = metric,
            maxPaths = maxPaths,
            isRiemannianManifold = isRiemannianManifold,
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
) {

    /**
     * Returns `true` if this path has been directly provided by a source
     * (namely, [source] == [through]).
     */
    val comesFromSource get() = source == through

    /**
     * Updates this path adding information about the local device.
     */
    @OptIn(DelicateCollektiveApi::class)
    inline fun updateDistance(
        neighbor: ID,
        distanceToNeighbor: Distance,
        bottom: Distance,
        top: Distance,
        crossinline accumulateDistance: Reducer<Distance>,
    ): UpdatedGradientPath<ID, Value, Distance> {
        val totalDistance = accumulateDistance(distance, distanceToNeighbor).coerceIn(bottom, top)
        check(totalDistance >= distance && totalDistance >= distanceToNeighbor) {
            "The provided distance accumulation function violates the triangle inequality: " +
                "accumulating $distance and $distanceToNeighbor produced $totalDistance"
        }
        return UpdatedGradientPath(neighbor, totalDistance, distanceToNeighbor, this)
    }
}

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

    val source get() = path.source

    /**
     * The neighbor's neighbor from which this gradient information is coming.
     */
    val neighborsNeighbor get() = path.through

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
