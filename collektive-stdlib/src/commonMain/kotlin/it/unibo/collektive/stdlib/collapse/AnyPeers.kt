/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import arrow.core.Option
import arrow.core.none
import arrow.core.some
import it.unibo.collektive.aggregate.CollapsePeers
import it.unibo.collektive.stdlib.util.Reducer

/**
 * Returns the peer entry (excluding local) that yields the largest value according to the given [selector].
 *
 * If multiple entries produce the same maximal value, one is returned arbitrarily.
 * If there are no peer entries, returns `null`.
 *
 * @param selector a function that maps each peer entry to a comparable value.
 * @return the peer entry with the highest value, or `null` if none.
 */
inline fun <T : Any, R : Comparable<R>> CollapsePeers<T>.maxBy(crossinline selector: (T) -> R): T? =
    maxWith(compareBy(selector))

/**
 * Returns the peer entry (excluding local) that yields the largest value according to the given [selector],
 * wrapped in an [Option].
 *
 * If multiple entries produce the same maximal value, one is returned arbitrarily.
 * If there are no peer entries, returns `none()`.
 *
 * @param selector a function that maps each peer entry to a comparable value.
 * @return an [Option] containing the peer entry with the highest value, or empty if none.
 */
inline fun <T, R : Comparable<R>> CollapsePeers<T>.maxBy(crossinline selector: (T) -> R): Option<T?> =
    maxWith(compareBy(selector))

/**
 * Returns the peer entry that yields the largest value according to the given [comparator].
 *
 * If multiple entries are considered equal under the comparator, one of them is returned arbitrarily.
 * If there are no peer entries, returns `null`.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the peer entry with the greatest value, or `null` if none.
 */
fun <T : Any> CollapsePeers<T>.maxWith(comparator: Comparator<in T>): T? = sequence.maxWithOrNull(comparator)

/**
 * Returns the peer entry (excluding local) that yields the smallest value according to the given [selector].
 *
 * If multiple entries produce the same minimal value, one is returned arbitrarily.
 * If there are no peer entries, returns `null`.
 *
 * @param selector a function that maps each peer entry to a comparable value.
 * @return the peer entry with the lowest value, or `null` if none.
 */
inline fun <T : Any, R : Comparable<R>> CollapsePeers<T>.minBy(crossinline selector: (T) -> R): T? =
    minWith(compareBy(selector))

/**
 * Returns the peer entry (excluding local) that yields the smallest value according to the given [selector],
 * wrapped in an [Option].
 *
 * If multiple entries produce the same minimal value, one is returned arbitrarily.
 * If there are no peer entries, returns `none()`.
 *
 * @param selector a function that maps each peer entry to a comparable value.
 * @return an [Option] containing the peer entry with the lowest value, or empty if none.
 */
inline fun <T, R : Comparable<R>> CollapsePeers<T>.minBy(crossinline selector: (T) -> R): Option<T?> =
    minWith(compareBy(selector))

/**
 * Returns the peer entry that yields the smallest value according to the given [comparator].
 *
 * If multiple entries are considered equal under the comparator, one of them is returned arbitrarily.
 * If there are no peer entries, returns `null`.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return the peer entry with the smallest value, or `null` if none.
 */
fun <T : Any> CollapsePeers<T>.minWith(comparator: Comparator<in T>): T? = sequence.minWithOrNull(comparator)

/**
 * Returns the peer entry (excluding local) that yields the smallest value according to the given [comparator],
 * wrapped in an [Option].
 *
 * If there are no peer entries, returns `none()`.
 *
 * @param comparator the comparator used to determine ordering between entries.
 * @return an [Option] containing the peer entry with the smallest value, or empty if none.
 */
fun <T> CollapsePeers<T>.minWith(comparator: Comparator<in T>): Option<T?> = reduce { a, b -> minOf(a, b, comparator) }

/**
 * Reduces the elements in this collapse (which excludes the local element, i.e., only peers) into a single value
 * by repeatedly applying [reducer].
 *
 * If there are no peer elements, returns `null`. Otherwise, behaves like a standard reduction over the peer sequence.
 *
 * @param reducer a binary operation that combines two values of type T into one.
 * @return the accumulated result of reducing the peer values, or `null` if the collapse is empty.
 */
inline fun <T : Any> CollapsePeers<T>.reduce(crossinline reducer: Reducer<T>): T? = sequence.reduceOrNull(reducer)

/**
 * Performs a reduction over the peer values in a collapsed computational field.
 *
 * A [CollapsePeers] represents the non-local portion of an aggregate computational field
 * turned into a local Kotlin collection. This function applies the provided binary [reducer]
 * to combine all peer elements into a single value, using the standard left-associative
 * reduction semantics of `Sequence.reduce`.
 *
 * If there are no peer elements, the result is `none()`. Otherwise the reduction result is
 * wrapped in `some(...)`.
 * For deterministic results across different execution orders, [reducer] should
 * ideally be associative.
 *
 * @param reducer a function that combines two peer values into one.
 * @return an [Option] containing the reduced value if any peers exist, or `none()` if the peer set is empty.
 */
inline fun <T> CollapsePeers<T>.reduce(crossinline reducer: Reducer<T>): Option<T?> = when {
    list.isEmpty() -> none()
    else -> sequence.reduce(reducer).some()
}
