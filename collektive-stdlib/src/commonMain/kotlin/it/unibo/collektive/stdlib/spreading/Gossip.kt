/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
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
import it.unibo.collektive.aggregate.values
import it.unibo.collektive.stdlib.collapse.reduce
import it.unibo.collektive.stdlib.ints.FieldedInts.toDouble
import it.unibo.collektive.stdlib.util.PathValue
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.coerceIn
import it.unibo.collektive.stdlib.util.hops
import it.unibo.collektive.stdlib.util.nonLoopingPaths
import it.unibo.collektive.stdlib.util.pathCoherence
import kotlin.jvm.JvmOverloads

/**
 * Executes a gossip-based aggregation algorithm. This method propagates and combines values
 * across devices based on their distance, applying a selection and accumulation strategy.
 *
 * @param local The local value of the device.
 * @param bottom The minimum distance value.
 * @param top The maximum distance value.
 * @param metric The distance field used to measure distances between devices.
 * @param maxDiameter The maximum allowable diameter for paths [default: Int.MAX_VALUE].
 * @param selector A function that determines how to select the preferred value during combination.
 *                 By default, it selects the first value encountered.
 * @param accumulateDistance The function used to combine distances during aggregation.
 * @return The final aggregated value after the gossiping process.
 */
@OptIn(DelicateCollektiveApi::class)
@JvmOverloads
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.gossip(
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline selector: (Value, Value) -> Value = { first, _ -> first }, // identity function
    crossinline accumulateDistance: Reducer<Distance>,
): Value {
    val coercedMetric = metric.coerceIn(bottom, top)
    val localCandidate = PathValue<ID, Value, Distance>(bottom, local, emptyList())
    return share(localCandidate) { candidate ->
        val nonLoopingPaths =
            nonLoopingPaths(candidate, coercedMetric, maxDiameter, bottom, top, { _, _, data ->
                selector(local, data)
            }, accumulateDistance)
        pathCoherence(nonLoopingPaths).fold(localCandidate) { current, next ->
            val candidateValue = selector(current.data, next.data)
            when {
                current.data == next.data -> listOf(current, next).minBy { it.hops.size }
                candidateValue == current.data -> current
                else -> next
            }
        }
    }.data ?: local
}

/**
 * Propagates a value across a network of devices using a gossip-based approach. The method selects
 * new values based on a provided selector function while computing distances using a specified metric.
 *
 * @param local the initial value held by the local device.
 * @param metric a function providing a distance metric between devices,
 * used to determine proximity during the gossip process.
 * @param selector a function used to select a value during the gossip process, given two candidate values.
 * @return the resulting value after the gossip process, based on the initial value,
 * the metric, and the selector function.
 */
@OptIn(DelicateCollektiveApi::class)
inline fun <reified ID : Any, reified Value, reified Distance : Comparable<Distance>> Aggregate<ID>.gossip(
    local: Value,
    metric: Field<ID, Double>,
    noinline selector: (Value, Value) -> Value,
): Value = gossip(
    local = local,
    bottom = 0.0,
    top = Double.POSITIVE_INFINITY,
    metric = metric,
    selector = selector,
    accumulateDistance = Double::plus,
)

/**
 * Executes a gossip-based aggregation process, leveraging hop distance as the metric
 * to propagate and combine values across devices in a network. Values are selected
 * and aggregated based on the provided selection strategy.
 *
 * @param ID The type of the device identifier.
 * @param Value The type of the values being aggregated.
 * @param local The local value of the device executing the hop gossip.
 * @param selector A function to determine the preferred value when combining.
 *                 It takes two values as input and returns the selected value.
 * @return The final aggregated value after the hop gossip process.
 */
@OptIn(DelicateCollektiveApi::class)
inline fun <reified ID : Any, reified Value> Aggregate<ID>.hopGossip(
    local: Value,
    noinline selector: (Value, Value) -> Value,
): Value = gossip(
    local = local,
    bottom = 0,
    top = Int.MAX_VALUE,
    metric = hops(),
    selector = selector,
    accumulateDistance = Int::plus,
)

/**
 * Computes the maximum value among devices in an aggregate system, using a gossip-based approach.
 *
 * @param local the local value to be compared with values from other devices.
 * @param bottom the minimal possible distance in the comparison range.
 * @param top the maximal possible distance in the comparison range.
 * @param metric a field representing the distance metric for comparison among devices.
 * @param maxDiameter an optional parameter to limit the maximum diameter of the gossip protocol.
 * @param accumulateDistance a function to reduce distances during the gossip process.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <
    reified ID : Any,
    reified Value : Comparable<Value>,
    reified Distance : Comparable<Distance>,
    > Aggregate<ID>.gossipMax(
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxDiameter: Int = Int.MAX_VALUE,
    crossinline accumulateDistance: Reducer<Distance>,
): Value = gossip(
    local = local,
    bottom = bottom,
    top = top,
    metric = metric,
    maxDiameter = maxDiameter,
    selector = ::maxOf,
    accumulateDistance = accumulateDistance,
)

/**
 * Computes the maximum value among devices in an aggregate system, using a gossip-based approach.
 *
 * @param local the local value to be compared against values from other devices.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <reified ID : Any, reified Value : Comparable<Value>> Aggregate<ID>.gossipMax(local: Value): Value =
    gossipMax(local, 0.0, Double.POSITIVE_INFINITY, hops().toDouble(), accumulateDistance = Double::plus)

/**
 * Aggregates and disseminates the minimum value of type [Value] using a gossiping protocol.
 * This method enables devices in a network to converge on the smallest value in a distributed fashion,
 * constrained by a specified distance metric.
 *
 * @param local the local value of type [Value] to contribute to the aggregation.
 * @param bottom the minimum value that can be considered for [Distance].
 * @param top the maximum value that can be considered for [Distance].
 * @param metric a field representing the distance metric to compare devices.
 * @param maxDiameter the maximum permissible network diameter for the gossiping operation, default is [Int.MAX_VALUE].
 * @param accumulateDistance the lambda function to reduce or accumulate distances of type [Distance].
 * @return the minimum value of type [Value] aggregated across the network.
 */
inline fun <
    reified ID : Any,
    reified Value : Comparable<Value>,
    reified Distance : Comparable<Distance>,
    > Aggregate<ID>.gossipMin(
    local: Value,
    bottom: Distance,
    top: Distance,
    metric: Field<ID, Distance>,
    maxDiameter: Int = Int.MAX_VALUE,
    crossinline accumulateDistance: Reducer<Distance>,
): Value = gossip(
    local = local,
    bottom = bottom,
    top = top,
    metric = metric,
    maxDiameter = maxDiameter,
    selector = ::minOf,
    accumulateDistance = accumulateDistance,
)

/**
 * Aggregates and disseminates the minimum value of type [Value]
 * using a gossiping protocol with a default distance configuration.
 * This method enables devices in a network to converge on the smallest value in a distributed manner,
 * using hop count as the distance metric.
 *
 * @param local the local value of type [Value] to contribute to the aggregation.
 * @return the minimum value of type [Value] aggregated across the network.
 */
inline fun <reified ID : Comparable<ID>, reified Value : Comparable<Value>> Aggregate<ID>.gossipMin(
    local: Value,
): Value = gossipMin(local, 0.0, Double.POSITIVE_INFINITY, hops().toDouble(), accumulateDistance = Double::plus)

/**
 * Determines if the given condition is satisfied on at least one device in the aggregate context.
 *
 * @param condition A lambda function representing the condition to be checked. It evaluates to true if the condition
 * is met on the current device, or false otherwise.
 * @return True if any device in the aggregate context satisfies the condition, false otherwise.
 */
inline fun <reified ID : Comparable<ID>> Aggregate<ID>.isHappeningAnywhere(condition: () -> Boolean): Boolean =
    gossipMax(condition())

/**
 * A **non-self-stabilizing** gossip function for repeated propagation of a [value] and [reducer]
 * of state estimates between neighboring devices.
 */
inline fun <ID : Any, reified Value> Aggregate<ID>.nonStabilizingGossip(
    value: Value,
    noinline reducer: Reducer<Value>,
): Value = share(value) { it.all.values.reduce(reducer) }

/**
 * A **non-self-stabilizing** function returning `true` if at any point in time a certain [condition] happened.
 *
 * *Note:* due to its non-self-stabilizing nature, if the [condition] does not hold anymore, this function will
 * keep returning `true`.
 * To check whether a condition is still holding, use [isHappeningAnywhere]
 */
fun <ID : Any> Aggregate<ID>.everHappened(condition: () -> Boolean): Boolean =
    nonStabilizingGossip(condition()) { a, b -> a || b }
