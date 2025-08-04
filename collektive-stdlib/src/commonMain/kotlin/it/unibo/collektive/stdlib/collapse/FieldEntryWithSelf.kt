/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import it.unibo.collektive.aggregate.CollapseWithSelf
import it.unibo.collektive.aggregate.FieldEntry

// TODO: add idOfMax and idOfMin for comparable field entries

/**
 * Returns the ID of the entry that yields the largest value according to the given [selector],
 * including local entry if applicable.
 *
 * @param selector a function that maps each [FieldEntry] to a comparable value.
 * @return the ID of the entry with the highest value.
 */
inline fun <ID : Any, T, R : Comparable<R>> CollapseWithSelf<FieldEntry<ID, T>>.idOfMaxBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): ID = maxBy(selector).id

/**
 * Returns the ID of the entry that yields the largest value according to the given [comparator],
 * including local entry if applicable.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the ID of the entry with the greatest ordering.
 */
fun <ID : Any, T> CollapseWithSelf<FieldEntry<ID, T>>.idOfMaxWith(comparator: Comparator<FieldEntry<ID, T>>): ID =
    maxWith(comparator).id

/**
 * Returns the ID of the entry that yields the smallest value according to the given [selector],
 * including local entry if applicable.
 *
 * @param selector a function that maps each [FieldEntry] to a comparable value.
 * @return the ID of the entry with the lowest value.
 */
inline fun <ID : Any, T, R : Comparable<R>> CollapseWithSelf<FieldEntry<ID, T>>.idOfMinBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): ID = minBy(selector).id

/**
 * Returns the ID of the entry that yields the smallest value according to the given [comparator],
 * including local entry if applicable.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the ID of the entry with the smallest ordering.
 */
fun <ID : Any, T> CollapseWithSelf<FieldEntry<ID, T>>.idOfMinWith(comparator: Comparator<FieldEntry<ID, T>>): ID =
    minWith(comparator).id

/**
 * Returns the value of the entry that yields the largest value according to the given [selector],
 * including local entry if applicable.
 *
 * @param selector a function that maps each [FieldEntry] to a comparable value.
 * @return the value of the entry with the highest score.
 */
inline fun <ID : Any, T, R : Comparable<R>> CollapseWithSelf<FieldEntry<ID, T>>.valueOfMaxBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): T = maxBy(selector).value

/**
 * Returns the value of the entry that yields the largest value according to the given [comparator],
 * including local entry if applicable.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the value of the entry with the greatest ordering.
 */
fun <ID : Any, T> CollapseWithSelf<FieldEntry<ID, T>>.valueOfMaxWith(comparator: Comparator<FieldEntry<ID, T>>): T =
    maxWith(comparator).value

/**
 * Returns the value of the entry that yields the smallest value according to the given [selector],
 * including local entry if applicable.
 *
 * @param selector a function that maps each [FieldEntry] to a comparable value.
 * @return the value of the entry with the lowest score.
 */
inline fun <ID : Any, T, R : Comparable<R>> CollapseWithSelf<FieldEntry<ID, T>>.valueOfMinBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): T = minBy(selector).value

/**
 * Returns the value of the entry that yields the smallest value according to the given [comparator],
 * including local entry if applicable.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the value of the entry with the smallest ordering.
 */
fun <ID : Any, T> CollapseWithSelf<FieldEntry<ID, T>>.valueOfMinWith(comparator: Comparator<FieldEntry<ID, T>>): T =
    minWith(comparator).value
