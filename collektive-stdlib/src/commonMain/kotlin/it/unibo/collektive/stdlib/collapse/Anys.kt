/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import arrow.core.Option
import arrow.core.Predicate
import it.unibo.collektive.aggregate.Collapse
import it.unibo.collektive.aggregate.CollapseNeighbors
import it.unibo.collektive.stdlib.util.Accumulator

/**
 * Utility extensions for performing common reductions and queries over a collapsed computational field.
 *
 * A [Collapse] represents turning a distributed aggregate field into a local Kotlin collection,
 * exposing its underlying sequence and list. These helpers provide predicates, counting, folding,
 * and peer-specific maximum extraction in a safe and ergonomic way.
 */

/**
 * Returns `true` if all elements in the collapsed field satisfy the given [predicate].
 *
 * Short-circuits on the first failure. If the field is empty, returns `true` (vacuous truth).
 *
 * @param predicate the condition to test each element against.
 * @return `true` if every element satisfies [predicate], `false` otherwise.
 */
inline fun <T> Collapse<T>.all(crossinline predicate: Predicate<T>): Boolean = sequence.all(predicate)

/**
 * Returns `true` if any element in the collapsed field satisfies the given [predicate].
 *
 * Short-circuits on the first success. If the field is empty, returns `false`.
 *
 * @param predicate the condition to test each element against.
 * @return `true` if at least one element satisfies [predicate], `false` otherwise.
 */
inline fun <T> Collapse<T>.any(crossinline predicate: Predicate<T>): Boolean = sequence.any(predicate)

/**
 * Counts how many elements in the collapsed field satisfy the given [predicate].
 *
 * @param predicate the condition to test each element against.
 * @return the number of elements matching [predicate].
 */
inline fun <T> Collapse<T>.countMatching(crossinline predicate: Predicate<T>): Int = sequence.count(predicate)

/**
 * Folds the collapsed field into a single value, starting from [initial] and combining elements
 * with the provided [accumulator].
 *
 * This is a general-purpose reduction; order is the standard left-to-right order of the sequence.
 *
 * @param initial the starting value for the fold.
 * @param accumulator a function that incorporates each element into the accumulating result.
 * @return the final accumulated result.
 */
inline fun <Destination, T> Collapse<T>.fold(
    initial: Destination,
    crossinline accumulator: Accumulator<Destination, T>,
): Destination = sequence.fold(initial, accumulator)

/**
 * Returns the peer element (excluding local) that is maximal according to the provided [comparator],
 * wrapped in an [Option].
 *
 * If there are no peer elements, returns `none()`. Ties are broken arbitrarily via the reduction order.
 *
 * @param comparator defines how two elements are compared to determine the maximum.
 * @return an [Option] containing the maximal peer element, or empty if no peers exist.
 */
fun <T> CollapseNeighbors<T>.maxWith(comparator: Comparator<in T>): Option<T?> =
    reduce { a, b -> maxOf(a, b, comparator) }

/**
 * Returns `true` if no element in the collapsed field satisfies the given [predicate].
 *
 * Equivalent to `!any(predicate)`. If the field is empty, returns `true`.
 *
 * @param predicate the condition to test each element against.
 * @return `true` if none of the elements satisfy [predicate], `false` otherwise.
 */
inline fun <T> Collapse<T>.none(crossinline predicate: Predicate<T>): Boolean = sequence.none(predicate)
