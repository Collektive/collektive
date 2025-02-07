/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.spreading

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.sharing
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.operations.contains
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
fun <ID : Any, Distance : Comparable<Distance>> Aggregate<ID>.distanceTo(
    source: Boolean,
    bottom: Distance,
    top: Distance,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateDistance: (Distance, Distance) -> Distance,
    metric: () -> Field<ID, Distance>,
): Distance =
    gradientCast(
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
fun <ID : Any> Aggregate<ID>.hopDistanceTo(
    source: Boolean,
    maxPaths: Int = Int.MAX_VALUE,
): Int = distanceTo(source, 0, Int.MAX_VALUE, maxPaths, Int::plus) { neighboring(1) }

/**
 * Compute the distance from the closest [source], using [Double]s.
 *
 * The distance between neighboring devices is computed using the [metric] function,
 * and defaults to the hop distance.
 */
@JvmOverloads
fun <ID : Any> Aggregate<ID>.distanceTo(
    source: Boolean,
    maxPaths: Int = Int.MAX_VALUE,
    metric: () -> Field<ID, Double> = { neighboring(1.0) },
): Double = distanceTo(source, 0.0, Double.POSITIVE_INFINITY, maxPaths, Double::plus, metric)

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
fun <ID : Any, Value, Distance : Comparable<Distance>> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Value,
    bottom: Distance,
    top: Distance,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    accumulateDistance: (fromSource: Distance, toNeighbor: Distance) -> Distance,
    metric: () -> Field<ID, Distance>,
): Value {

    class Path {

        val distance: Distance
        val neighbor: ID
        val source: ID
        val data: Value

        data class Source(val source: ID, val data) : Path
    }

    @Serializable
    data class GradientPath private constructor (
        val distance: Distance,
        val neighbor: ID,
        val source: ID,
        val data: Value,
    ) {

        constructor(distance: Distance, neighbor: ID, source: ID, data: Value) : this(distance, neighbor, source, data)
    }

    data class UpdatedGradientPath(
        val neighbor: ID,
        val total: Distance,
        val toNeighbor: Distance,
        val path: GradientPath,
    ) : Comparable<UpdatedGradientPath> {
        override fun compareTo(other: UpdatedGradientPath): Int = total.compareTo(other.total)

        fun toLocalPath(): GradientPath =
            GradientPath(total, neighbor, path.source, accumulateData(path.distance, toNeighbor, path.data))
    }
    require(maxPaths > 0) {
        "The maximum number of paths must be positive"
    }
    val fromLocalSource = if (source) listOf(GradientPath(bottom, localId, localId, local)) else emptyList()
    return sharing(fromLocalSource) { neighborData ->
        /*
         * Multi-path gradient repair: share a set of paths, invalidate those for which the last step contains
         * this node or any neighbor.
         * Respecting the max size (maxPaths), find the shortest path towards each source
         * and alternative paths towards the same source.
         */
        val neighbors = neighborData.neighbors
        val validPaths =
            neighborData.map { paths ->
                paths.filter { (_, neighbor, _, _) ->
                    neighbor == source ||
                        neighbor != localId &&
                        neighbor !in neighbors
                }
            }
        val distanceUpdatedPaths =
            validPaths
                .alignedMapWithId(metric().coerceIn(bottom, top)) { id, pathsThroughNeighbor, distanceToNeighbor ->
                    pathsThroughNeighbor.map { path ->
                        val totalDistance = accumulateDistance(path.distance, distanceToNeighbor).coerceIn(bottom, top)
                        check(totalDistance >= path.distance) {
                            "The provided distance accumulation function violates the triangle inequality: " +
                                "accumulating ${path.distance} and $distanceToNeighbor produced $totalDistance"
                        }
                        UpdatedGradientPath(id, totalDistance, distanceToNeighbor, path)
                    }
                }.fold(mutableListOf<UpdatedGradientPath>()) { list, paths ->
                    list.apply { addAll(paths) }
                }.sorted()
        println(distanceUpdatedPaths)
        var sharedPaths = fromLocalSource + when {
            // We can keep all paths
            distanceUpdatedPaths.size < maxPaths -> distanceUpdatedPaths.map { it.toLocalPath() }
            else -> {
                ArrayList<GradientPath>(maxPaths).apply {
                    /*
                     * Pick the paths by priority:
                     * 1. prepare a list of results, large at most multipaths;
                     * 2. classify them by source in a Map<ID, List<UpdatedGradientPath>>;
                     * 3. pick the shortest for each source, removing it from the list;
                     * 4. sort the selected ones by distance, and pick at most multipaths of them.
                     * 5. if there are more slots, goto 2.
                     */
                    val pathsBySource =
                        mutableMapOf<ID, MutableList<UpdatedGradientPath>>().withDefault { mutableListOf() }
                    // Equivalent to a groupBy, but presumably faster with large networks
                    distanceUpdatedPaths.forEach {
                        // Since the source collection is sorted, the sub-collections will be sorted as well
                        pathsBySource[it.path.source] =
                            pathsBySource.getValue(it.path.source).apply {
                                add(it)
                            }
                    }
                    while (size < maxPaths) {
                        val candidates =
                            pathsBySource.values
                                .mapNotNull { it.removeFirstOrNull() }
                                .take(maxPaths - size)
                                .map { it.toLocalPath() }
                        addAll(candidates)
                    }
                }
            }
        }
        sharedPaths.distinct().take(maxPaths).yielding { sharedPaths.firstOrNull()?.data ?: local }
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
fun <ID : Any, Type> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Type,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateData: (fromSource: Int, toNeighbor: Int, data: Type) -> Type = { _, _, data -> data },
    metric: () -> Field<ID, Int> = { neighboring(1) },
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
fun <ID : Any, Type> Aggregate<ID>.gradientCast(
    source: Boolean,
    local: Type,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateData: (fromSource: Double, toNeighbor: Double, data: Type) -> Type = { _, _, data -> data },
    metric: () -> Field<ID, Double> = { neighboring(1.0) },
): Type = gradientCast(source, local, 0.0, Double.POSITIVE_INFINITY, maxPaths, accumulateData, Double::plus, metric)

/**
 * Provided a list of [sources], propagates information from each, collecting it in a map.
 *
 * If there are no sources and no neighbors, default to [local] value.
 * The [metric] function is used to compute the distance between devices in form of a field of [Float]s.
 * [accumulateData] is used to modify data from neighbors on the fly, and defaults to the identity function.
 */
@JvmOverloads
fun <ID : Any, Value, Distance : Comparable<Distance>> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    bottom: Distance,
    top: Distance,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateData: (fromSource: Distance, toNeighbor: Distance, data: Value) -> Value =
        { _, _, data -> data },
    accumulateDistance: (Distance, Distance) -> Distance,
    metric: () -> Field<ID, Distance>,
): Map<ID, Value> =
    sources.associateWith { source ->
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
fun <ID : Any, Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateData: (fromSource: Double, toNeighbor: Double, data: Value) -> Value = { _, _, data -> data },
    metric: () -> Field<ID, Double> = { neighboring(1.0) },
): Map<ID, Value> =
    sources.associateWith { source ->
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
fun <ID : Any, Value> Aggregate<ID>.multiGradientCast(
    sources: Iterable<ID>,
    local: Value,
    maxPaths: Int = Int.MAX_VALUE,
    accumulateData: (fromSource: Int, toNeighbor: Int, data: Value) -> Value = { _, _, data -> data },
    metric: () -> Field<ID, Int> = { neighboring(1) },
): Map<ID, Value> =
    sources.associateWith { source ->
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
