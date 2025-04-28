/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.stdlib.accumulation

import arrow.core.None
import arrow.core.Predicate
import arrow.core.Some
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.exchanging
import it.unibo.collektive.aggregate.api.mapNeighborhood
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.fields.collectIDsMatching
import it.unibo.collektive.stdlib.fields.foldValues
import it.unibo.collektive.stdlib.fields.minWith
import it.unibo.collektive.stdlib.spreading.hopDistanceTo
import it.unibo.collektive.stdlib.util.IncludingSelf
import it.unibo.collektive.stdlib.util.Maybe
import it.unibo.collektive.stdlib.util.Maybe.Companion.merge
import it.unibo.collektive.stdlib.util.Maybe.Companion.some
import kotlin.contracts.ExperimentalContracts
import kotlin.jvm.JvmOverloads

/**
 * Aggregate a field of [local] [Data] along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator which,
 * by default, selects the parent with *lowest* potential.
 * [Data] is accumulated using the [accumulateData] function.
 */
@JvmOverloads
inline fun <reified ID : Any, reified Data, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeCast(
    local: Data,
    potential: Field<ID, Potential>,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
    crossinline accumulateData: (Data, Data) -> Data,
): Data = exchanging<ID, Maybe<Data>, Data>(Maybe.none) { data: Field<ID, Maybe<Data>> ->
    val localValue: Maybe<Data> = data
        .foldValues(some(local)) { current, new -> current.merge(new, accumulateData) }
    check(localValue.option is Some) {
        "Bug in the implementation of convergeCast. A reduction using as base value ${some(local)} produced $None"
    }
    val parent: ID = findParent(potential, selectParent)
    // This anisotropic field contains the values to be sent to neighbors,
    // namely, it is a field for which solely the parents receive the local value
    val messages: Field<ID, Maybe<Data>> = mapNeighborhood { id ->
        when {
            parent == localId -> Maybe.none // I'm the leader, I don't contribute to feeding data to other devices
            id == parent -> localValue // this is my parent, I'm sending them data
            else -> Maybe.none // "sibling" device
        }
    }
    messages.yielding { localValue.option.value }
}

/**
 * Aggregate a field of [local] [Data] along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator which,
 * by default, selects the parent with *lowest* potential.
 * [Data] is accumulated using the [accumulateData] function.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Data, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeCast(
    local: Data,
    potential: Potential,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
    crossinline accumulateData: (Data, Data) -> Data,
): Data = convergeCast(local, neighboring(potential), selectParent, accumulateData)

/**
 * Aggregate a field of [local] [Data] into the closest [sink] along a spanning tree built using [hopDistanceTo].
 * [Data] is accumulated using the [accumulateData] function.
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Data> Aggregate<ID>.convergeCast(
    local: Data,
    sink: Boolean,
    crossinline accumulateData: (Data, Data) -> Data,
): Data = convergeCast<ID, Data, Int>(local, hopDistanceTo(sink), defaultComparator(), accumulateData)

/**
 * Computes the mean of all [local] values along a spanning tree
 * built descending the provided [potential] field using [selectParent].
 */
@JvmOverloads
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeMean(
    local: Double,
    potential: Field<ID, Potential>,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Double = convergeSum(local, potential, selectParent) / countDevices(potential, selectParent)

/**
 * Computes the mean of all [local] values along a spanning tree
 * built descending the provided [potential] field using [selectParent].
 */
@JvmOverloads
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeMean(
    local: Double,
    potential: Potential,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Double = convergeMean(local, neighboring(potential), selectParent)

/**
 * Computes the mean of all [local] values into the selected [sink]
 * along a spanning tree built by descending the potential built using [hopDistanceTo].
 */
inline fun <reified ID : Any> Aggregate<ID>.convergeMean(local: Double, sink: Boolean): Double =
    convergeMean(local, hopDistanceTo(sink))

/**
 * Sums all [local] [Int]s along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeSum(
    local: Int,
    potential: Field<ID, Potential>,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Int = convergeCast(local, potential, selectParent, Int::plus)

/**
 * Sums all [local] [Double]s along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeSum(
    local: Double,
    potential: Field<ID, Potential>,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Double = convergeCast(local, potential, selectParent, Double::plus)

/**
 * Sums all [local] [Int]s along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeSum(
    local: Int,
    potential: Potential,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Int = convergeSum(local, neighboring(potential), selectParent)

/**
 * Sums all [local] [Double]s along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeSum(
    local: Double,
    potential: Potential,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Double = convergeSum(local, neighboring(potential), selectParent)

/**
 * Sums all [local] [Int]s into the closest [sink] along a spanning tree built using [hopDistanceTo].
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any> Aggregate<ID>.convergeSum(local: Int, sink: Boolean): Int =
    convergeSum(local, hopDistanceTo(sink))

/**
 * Sums all [local] [Double]s into the closest [sink] along a spanning tree built using [hopDistanceTo].
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any> Aggregate<ID>.convergeSum(local: Double, sink: Boolean): Double =
    convergeSum(local, hopDistanceTo(sink))

/**
 * Counts the number of devices in the network along a spanning tree
 * built descending the provided [potential] field using the [selectParent] [Comparator].
 * For all devices to get counted, there must be a single global minimum.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.countDevices(
    potential: Field<ID, Potential>,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Int = convergeSum(1, potential, selectParent)

/**
 * Counts the number of devices in the network along a spanning tree
 * built descending the provided [potential] field using the [selectParent] [Comparator].
 * For all devices to get counted, there must be a single global minimum.
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.countDevices(
    potential: Potential,
    selectParent: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): Int = countDevices(neighboring(potential), selectParent)

/**
 * Counts the number of devices in the network along a spanning tree
 * built using [hopDistanceTo] starting from [sink].
 * For all devices to get counted, there must be a single [sink].
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified ID : Any> Aggregate<ID>.countDevices(sink: Boolean): Int = countDevices(hopDistanceTo(sink))

@PublishedApi
internal inline fun <reified ID : Any, reified P : Comparable<P>> defaultComparator(): Comparator<FieldEntry<ID, P>> =
    compareBy<FieldEntry<ID, P>> { it.value }

/**
 * Find the best neighbor of the current device along a [potential] field,
 * selecting it with the provided [comparator] function by returning the **minimum**.
 *
 * By default, the neighbor with the **lowest** potential is selected.
 *
 * If there are no neighbors, or the local device is the best one, its ID is returned.
 */
@JvmOverloads
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParent(
    potential: Field<ID, Potential>,
    comparator: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): ID = potential.minWith(IncludingSelf, comparator)?.id ?: localId

/**
 * Find the best neighbor of the current device along a [potential] field,
 * selecting it with the provided [comparator] function by returning the **minimum**.
 *
 * By default, the neighbor with the **lowest** potential is selected.
 *
 * If there are no neighbors, or the local device is the best one, its ID is returned.
 */
@JvmOverloads
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParent(
    potential: Potential,
    comparator: Comparator<FieldEntry<ID, Potential>> = defaultComparator(),
): ID = findParent(neighboring(potential), comparator)

/**
 * Find the parents of the current device along a [potential] field.
 * Only neighbors that satisfy [isParent] are considered as parents.
 * By default, [isParent] returns all devices with a potential **smaller** than the local one.
 */
@JvmOverloads
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParents(
    potential: Field<ID, Potential>,
    crossinline isParent: Predicate<FieldEntry<ID, Potential>> = { potential.local.value > it.value },
): Set<ID> = potential.collectIDsMatching(predicate = isParent)

/**
 * Find the parents of the current device along a [potential] field.
 * Only neighbors that satisfy [isParent] are considered as parents.
 * By default, [isParent] returns all devices with a potential **smaller** than the local one.
 */
@JvmOverloads
inline fun <reified ID : Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParents(
    potential: Potential,
    crossinline isParent: Predicate<FieldEntry<ID, Potential>> = { potential > it.value },
): Set<ID> = findParents(neighboring(potential), isParent)
