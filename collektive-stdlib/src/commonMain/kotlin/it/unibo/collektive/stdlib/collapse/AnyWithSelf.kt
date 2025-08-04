/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import it.unibo.collektive.aggregate.CollapseWithSelf
import it.unibo.collektive.stdlib.util.Reducer

/**
 * Returns the entry that yields the largest value according to the given [selector],
 * including the local entry if the receiver is configured to do so.
 *
 * If multiple entries produce the same maximal value, one is returned arbitrarily.
 * This will throw if there are no entries (since local is included or excluded depending on configuration).
 *
 * @param selector a function that maps each entry to a comparable value.
 * @return the entry with the highest value as determined by [selector].
 */
inline fun <T, R : Comparable<R>> CollapseWithSelf<T>.maxBy(crossinline selector: (T) -> R): T =
    maxWith(compareBy(selector))

/**
 * Returns the entry that yields the largest value according to the given [comparator],
 * including local entry if the receiver is configured to do so.
 *
 * If multiple entries are considered equal under the comparator, one of them is returned arbitrarily.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the entry with the greatest value.
 */
fun <T> CollapseWithSelf<T>.maxWith(comparator: Comparator<in T>): T = sequence.maxWith(comparator)

/**
 * Returns the entry that yields the smallest value according to the given [selector],
 * including the local entry if the receiver is configured to do so.
 *
 * If multiple entries produce the same minimal value, one is returned arbitrarily.
 * This will throw if there are no entries (since local is included or excluded depending on configuration).
 *
 * @param selector a function that maps each entry to a comparable value.
 * @return the entry with the lowest value as determined by [selector].
 */
inline fun <T, R : Comparable<R>> CollapseWithSelf<T>.minBy(crossinline selector: (T) -> R): T =
    minWith(compareBy(selector))

/**
 * Returns the entry that yields the smallest value according to the given [comparator],
 * including local entry if the receiver is configured to do so.
 *
 * If multiple entries are considered equal under the comparator, one of them is returned arbitrarily.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the entry with the smallest value.
 */
fun <T> CollapseWithSelf<T>.minWith(comparator: Comparator<in T>): T = sequence.minWith(comparator)

/**
 * Reduces the elements in this collapse (which includes the local element and peers) into a single value
 * by repeatedly applying [reducer].
 *
 * Because the local element is always present in a CollapseWithSelf, this will always return a value.
 *
 * @param reducer a binary operation that combines two values of type T into one.
 * @return the accumulated result of reducing all elements.
 */
inline fun <T> CollapseWithSelf<T>.reduce(crossinline reducer: Reducer<T>): T = sequence.reduce(reducer)
