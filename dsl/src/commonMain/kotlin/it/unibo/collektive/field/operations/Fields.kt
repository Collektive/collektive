@file:Suppress("TooManyFunctions")

package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import kotlin.jvm.JvmOverloads

/**
 * Count the number of elements in the field that satisfy the [predicate],
 * ignoring the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.count(crossinline predicate: (T) -> Boolean = { true }): Int =
    fold(0) { acc, value -> if (predicate(value)) acc + 1 else acc }

/**
 * Count the number of elements in the field that satisfy the [predicate],
 * including the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.countWithSelf(crossinline predicate: (T) -> Boolean = { true }): Int =
    count(predicate) + if (predicate(localValue)) 1 else 0

/**
 * Check if all the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.all(crossinline predicate: (T) -> Boolean): Boolean =
    fold(true) { acc, value -> acc && predicate(value) }

/**
 * Check if all the elements in the field satisfy the [predicate],
 * including the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.allWithSelf(crossinline predicate: (T) -> Boolean): Boolean =
    all(predicate) && predicate(localValue)

/**
 * Check if any of the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.any(crossinline predicate: (T) -> Boolean): Boolean =
    fold(false) { acc, value -> acc || predicate(value) }

/**
 * Check if any of the elements in the field satisfy the [predicate],
 * including the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.anyWithSelf(crossinline predicate: (T) -> Boolean): Boolean =
    any(predicate) || predicate(localValue)

/**
 * Returns the element yielding the largest value of the given [comparator].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 */
fun <ID : Any, T> Field<ID, T>.maxWith(base: T, comparator: Comparator<T>): T = minWith(base, comparator.reversed())

/**
 * Returns the element yielding the largest value of the given [selector].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxBy(base: T, crossinline selector: (T) -> R): T =
    maxWith(base, compareBy(selector))

/**
 * Returns the element yielding the smallest value of the given [comparator].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 */
fun <ID : Any, T> Field<ID, T>.minWith(base: T, comparator: Comparator<T>): T =
    fold(base) { acc, value -> if (comparator.compare(acc, value) < 0) acc else value }

/**
 * Returns the element yielding the smallest value of the given [selector].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minBy(base: T, crossinline selector: (T) -> R): T =
    minWith(base, compareBy(selector))

/**
 * Check if none of the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.none(crossinline predicate: (T) -> Boolean): Boolean = !any(predicate)

/**
 * Check if none of the elements in the field satisfy the [predicate],
 * including the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.noneWithSelf(crossinline predicate: (T) -> Boolean): Boolean =
    !anyWithSelf(predicate)

/**
 * Returns a new field containing [replaceWith] for each element that satisfies the [predicate].
 */
inline fun <ID : Any, T> Field<ID, T>.replaceMatchingWithId(
    replaceWith: T,
    crossinline predicate: (ID, T) -> Boolean,
): Field<ID, T> = mapWithId { id, value -> if (predicate(id, value)) replaceWith else value }

/**
 * Returns a new field containing [replaceWith] for each element that satisfies the [predicate].
 */
inline fun <ID : Any, T> Field<ID, T>.replaceMatching(
    replaceWith: T,
    crossinline predicate: (T) -> Boolean,
): Field<ID, T> = replaceMatchingWithId(replaceWith) { _, value -> predicate(value) }
