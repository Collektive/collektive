/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry

/**
 * Returns the entry that yields the smallest value according to the given [selector],
 * excluding the local entry.
 *
 * If multiple entries produce the same minimal value, the result is undefined.
 * If the field contains only the local entry, the result is `null`.
 *
 * @param selector a function that maps each entry to a comparable value.
 * @return the entry with the lowest value as determined by [selector], or `null` if none.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): FieldEntry<ID, T>? = reduce { a, b -> minOf(a, b, compareBy(selector)) }

/**
 * Returns the ID of the entry that yields the smallest value according to the given [selector],
 * excluding the local entry.
 *
 * If multiple entries produce the same minimal value, the result is undefined.
 * If the field contains only the local entry, the result is `null`.
 *
 * @param selector a function that maps each entry to a comparable value.
 * @return the ID of the entry with the lowest value, or `null` if none.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minIDBy(crossinline selector: (FieldEntry<ID, T>) -> R): ID? =
    minBy(selector)?.id

/**
 * Returns the value of the entry that yields the smallest value according to the given [selector],
 * excluding the local entry.
 *
 * If multiple entries produce the same minimal value, the result is undefined.
 * If the field contains only the local entry, the result is `null`.
 *
 * @param selector a function that maps each entry to a comparable value.
 * @return the value of the entry with the lowest score, or `null` if none.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minValueBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): T? = minBy(selector)?.value

/**
 * Returns the entry that yields the smallest value according to the given [comparator],
 * excluding the local entry.
 *
 * If multiple entries are considered equal under the comparator, one is returned arbitrarily.
 * If the field contains only the local entry, the result is `null`.
 *
 * @param comparator a comparator that defines the ordering of entries.
 * @return the entry with the lowest value by [comparator], or `null` if none.
 */
fun <ID : Any, T> Field<ID, T>.minWith(comparator: Comparator<FieldEntry<ID, T>>): FieldEntry<ID, T>? =
    maxWith(comparator.reversed())

/**
 * Returns the value of the entry that yields the smallest value according to the given [comparator],
 * excluding the local entry.
 *
 * If multiple entries are considered equal under the comparator, one is returned arbitrarily.
 * If the field contains only the local entry, the result is `null`.
 *
 * @param comparator a comparator that defines the ordering of entries.
 * @return the value of the entry with the lowest ordering, or `null` if none.
 */
fun <ID : Any, T> Field<ID, T>.minValueWith(comparator: Comparator<FieldEntry<ID, T>>): T? = minWith(comparator)?.value

/**
 * Returns the ID of the entry that yields the smallest value according to the given [comparator],
 * excluding the local entry.
 *
 * If multiple entries are considered equal under the comparator, one is returned arbitrarily.
 * If the field contains only the local entry, the result is `null`.
 *
 * @param comparator a comparator that defines the ordering of entries.
 * @return the ID of the entry with the lowest ordering, or `null` if none.
 */
fun <ID : Any, T> Field<ID, T>.minIDWith(comparator: Comparator<FieldEntry<ID, T>>): ID? = minWith(comparator)?.id
