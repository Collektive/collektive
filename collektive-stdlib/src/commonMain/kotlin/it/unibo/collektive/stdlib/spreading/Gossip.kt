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
import it.unibo.collektive.aggregate.values
import it.unibo.collektive.stdlib.collapse.reduce
import it.unibo.collektive.stdlib.util.PathValue
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.hops
import it.unibo.collektive.stdlib.util.nonLoopingPaths
import it.unibo.collektive.stdlib.util.pathCoherence
import kotlin.jvm.JvmOverloads

/**
 * Executes a gossip-based aggregation algorithm. This method propagates and combines values
 * across devices based on their distance, applying a selection and accumulation strategy.
 *
 * @param local The local value of the device.
 * @param maxDiameter The maximum allowable diameter for paths [default: Int.MAX_VALUE].
 * @param reducer A function that determines how to select the preferred value during combination.
 *                 By default, it selects the first value encountered.
 * @return The final aggregated value after the gossiping process.
 */
@OptIn(DelicateCollektiveApi::class)
@JvmOverloads
inline fun <reified ID : Comparable<ID>, reified Value> Aggregate<ID>.gossip(
    local: Value,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline reducer: Reducer<Value> = { first, _ -> first }, // identity function
): Value {
    val localCandidate = PathValue<ID, Value, Int>(0, local, emptyList())
    return share(localCandidate) { candidate ->
        val nonLoopingPaths = nonLoopingPaths(
            candidate,
            hops(),
            maxDiameter,
            0,
            Int.MAX_VALUE,
            { _, _, data -> reducer(local, data) },
            Int::plus,
        )
        pathCoherence(nonLoopingPaths).fold(localCandidate) { current, next ->
            val candidateValue = reducer(current.data, next.data)
            when {
                current.data == next.data -> listOf(current, next).minBy { it.hops.size }
                candidateValue == current.data -> current
                else -> next
            }
        }
    }.data ?: local
}

/**
 * Computes the maximum value among devices according to a specified comparator.
 *
 * @param local the local value to be compared with values from other devices.
 * @param comparator a comparator to determine the ordering of the values.
 * @param maxDiameter an optional parameter to limit the maximum diameter of the gossip protocol.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <reified ID, reified Value> Aggregate<ID>.gossipMaxWith(
    local: Value,
    comparator: Comparator<Value>,
    maxDiameter: Int = Int.MAX_VALUE,
): Value where ID : Comparable<ID> = gossip(
    local = local,
    maxDiameter = maxDiameter,
) { first, second -> listOf(first, second).maxWith(comparator) }

/**
 * Computes the maximum value among devices according to a specified selector function.
 *
 * @param local the local value to be compared with values from other devices.
 * @param maxDiameter an optional parameter to limit the maximum diameter of the gossip protocol.
 * @param selector a function that extracts a comparable component from the value for comparison.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <reified ID, reified Value, C> Aggregate<ID>.gossipMaxBy(
    local: Value,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline selector: (Value) -> C,
): Value where ID : Comparable<ID>, C : Comparable<C> = gossipMaxWith(
    local = local,
    comparator = compareBy(selector),
    maxDiameter = maxDiameter,
)

/**
 * Computes the maximum value among devices in an aggregate system, using a gossip-based approach.
 *
 * @param local the local value to be compared with values from other devices.
 * @param maxDiameter an optional parameter to limit the maximum diameter of the gossip protocol.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <reified ID, reified Value> Aggregate<ID>.gossipMax(
    local: Value,
    maxDiameter: Int = Int.MAX_VALUE,
): Value where ID : Comparable<ID>, Value : Comparable<Value> = gossipMaxBy(local, maxDiameter) { it }

/**
 * Computes the minimum value among devices according to a specified comparator.
 *
 * @param local the local value to be compared with values from other devices.
 * @param comparator a comparator to determine the ordering of the values.
 * @param maxDiameter an optional parameter to limit the maximum diameter of the gossip protocol.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <reified ID, reified Value> Aggregate<ID>.gossipMinWith(
    local: Value,
    comparator: Comparator<Value>,
    maxDiameter: Int = Int.MAX_VALUE,
): Value where ID : Comparable<ID> = gossipMaxWith(local, comparator.reversed(), maxDiameter)

/**
 * Computes the minimum value among devices according to a specified selector function.
 *
 * @param local the local value to be compared with values from other devices.
 * @param maxDiameter an optional parameter to limit the maximum diameter of the gossip protocol.
 * @param selector a function that extracts a comparable component from the value for comparison.
 * @return the maximum value among devices in the context of the aggregate system.
 */
inline fun <reified ID, reified Value, C> Aggregate<ID>.gossipMinBy(
    local: Value,
    maxDiameter: Int = Int.MAX_VALUE,
    noinline selector: (Value) -> C,
): Value where ID : Comparable<ID>, C : Comparable<C> = gossipMinWith(
    local = local,
    comparator = compareBy(selector),
    maxDiameter = maxDiameter,
)

/**
 * Aggregates and disseminates the minimum value of type [Value] using a gossiping protocol.
 * This method enables devices in a network to converge on the smallest value in a distributed fashion.
 *
 * @param local the local value of type [Value] to contribute to the aggregation.
 * @param maxDiameter the maximum permissible network diameter for the gossiping operation, default is [Int.MAX_VALUE].
 * @return the minimum value of type [Value] aggregated across the network.
 */
inline fun <reified ID : Comparable<ID>, reified Value : Comparable<Value>> Aggregate<ID>.gossipMin(
    local: Value,
    maxDiameter: Int = Int.MAX_VALUE,
): Value = gossipMinBy(local, maxDiameter) { it }

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
