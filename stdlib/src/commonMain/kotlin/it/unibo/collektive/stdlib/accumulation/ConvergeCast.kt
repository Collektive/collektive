/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.accumulation

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.collectIDs
import it.unibo.collektive.field.operations.minWithId
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

/**
 * Aggregate a field of [local] [Data] along a spanning tree built according descending the provided [potential] field.
 * The parent of the current node is selected by picking the minimum as provided by the [selectParent] comparator.
 * [Data] is accumulated using the [accumulateData] function.
 */
@OptIn(ExperimentalContracts::class)
inline fun <reified ID: Any, reified Data, reified Potential : Comparable<Potential>> Aggregate<ID>.convergeCast(
    local: Data,
    crossinline potential: () -> Field<ID, Potential>,
    selectParent: Comparator<Pair<ID, Potential>>,
    crossinline accumulateData: (Data, Data) -> Data
): Data {
    contract { callsInPlace(potential, InvocationKind.EXACTLY_ONCE) }
    val neighborPotential = potential()
    return share(local) { data ->
        val parent = findParent(neighborPotential, selectParent)
        accumulateData(local, data[parent])
    }
}

/**
 * Find the best neighbor of the current device along a [potential] field,
 * selecting it with the provided [comparator] function by returning the **minimum**.
 *
 * By default, the neighbor with the **lowest** potential is selected.
 *
 * If there are no neighbors, or the local device is the best one, its ID is returned.
 */
@JvmOverloads
inline fun <reified ID: Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParent(
    potential: Field<ID, Potential>,
    comparator: Comparator<Pair<ID, Potential>> = compareBy<Pair<ID, Potential>> { it.second },
): ID = potential.minWithId(localId to potential.localValue, comparator).first

/**
 * Find the best neighbor of the current device along a [potential] field,
 * selecting it with the provided [comparator] function by returning the **minimum**.
 *
 * By default, the neighbor with the **lowest** potential is selected.
 *
 * If there are no neighbors, or the local device is the best one, its ID is returned.
 */
@JvmOverloads
inline fun <reified ID: Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParent(
    potential: Potential,
    comparator: Comparator<Pair<ID, Potential>> = compareBy<Pair<ID, Potential>> { it.second },
): ID = findParent(neighboring(potential), comparator)

/**
 * Find the parents of the current device along a [potential] field.
 * Only neighbors that satisfy [isParent] are considered as parents.
 * By default, [isParent] returns all devices with a potential **smaller** than the local one.
 */
@JvmOverloads
inline fun <reified ID: Any, reified Potential : Comparable<Potential>> Aggregate<ID>.findParents(
    potential: Potential,
    crossinline isParent: (neighbor: ID, neighborPotential: Potential) -> Boolean = { _, other -> potential > other },
): Set<ID> = neighboring(potential).collectIDs(isParent)
