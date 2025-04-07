/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:Suppress("TooManyFunctions")

package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.foldWithId
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

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
    predicate(localValue) || any(predicate)

/**
 * Returns the [Collection] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
@OptIn(ExperimentalContracts::class)
@PublishedApi
internal inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.genericCollect(
    produceAccumulator: (Int) -> C,
    includeSelf: Boolean,
    crossinline predicate: (ID, T) -> Boolean,
    crossinline transform: (ID, T) -> R,
): C {
    contract {
        callsInPlace(produceAccumulator, InvocationKind.EXACTLY_ONCE)
    }
    val collected = produceAccumulator(neighborsCount + if (includeSelf) 1 else 0)
    if (includeSelf && predicate(localId, localValue)) {
        collected.add(transform(localId, localValue))
    }
    foldWithId(collected) { _, id, element: T ->
        if (predicate(id, element)) collected.add(transform(id, element))
        collected
    }
    return collected
}

/**
 * Returns the [Collection] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
@PublishedApi
internal inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.collectInternal(
    includeSelf: Boolean,
    crossinline predicate: (ID, T) -> Boolean,
    crossinline transform: (ID, T) -> R,
): List<R> = genericCollect(
    produceAccumulator = { ArrayList(it) },
    includeSelf = includeSelf,
    predicate = predicate,
    transform = transform,
)

/**
 * Returns the [List] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
@PublishedApi
internal inline fun <ID : Any, T, R> Field<ID, T>.collectDistinctInternal(
    includeSelf: Boolean,
    crossinline predicate: (ID, T) -> Boolean,
    crossinline transform: (ID, T) -> R,
): Set<R> = genericCollect(
    produceAccumulator = { mutableSetOf() },
    includeSelf = includeSelf,
    predicate = predicate,
    transform = transform,
)

/**
 * Returns the list of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
inline fun <ID : Any, T, R> Field<ID, T>.collect(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
    crossinline transform: (ID, T) -> R,
): List<R> = collectInternal(false, predicate, transform)

/**
 * Returns the list of field elements that satisfy the [predicate].
 * ignoring the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collect(crossinline predicate: (ID, T) -> Boolean = { _, _ -> true }): List<T> =
    collect(predicate) { _, value -> value }

/**
 * Returns the [Set] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
inline fun <ID : Any, T, R> Field<ID, T>.collectDistinct(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
    crossinline transform: (ID, T) -> R,
): Set<R> = collectDistinctInternal(false, predicate, transform)

/**
 * Returns the [Set] of field elements that satisfy the [predicate],
 * ignoring the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinct(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
): Set<T> = collectDistinct(predicate) { _, value -> value }

/**
 * Returns the [Set] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
inline fun <ID : Any, T, R> Field<ID, T>.collectDistinctWithSelf(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
    crossinline transform: (ID, T) -> R,
): Set<R> = collectDistinctInternal(true, predicate, transform)

/**
 * Returns the [Set] of field elements that satisfy the [predicate],
 * ignoring the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctWithSelf(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
): Set<T> = collectDistinctWithSelf(predicate) { _, value -> value }

/**
 * Returns the [Set] of neighbor [ID]s whose field entries satisfy the [predicate].
 * ignoring the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectIDs(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
): Set<ID> = collectDistinct(predicate) { id, _ -> id }

/**
 * Returns the [Set] of neighbor [ID]s whose field entries satisfy the [predicate].
 * ignoring the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectIDsWithSelf(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
): Set<ID> = collectDistinctWithSelf(predicate) { id, _ -> id }

/**
 * Returns the list of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * including the local value.
 */
inline fun <ID : Any, T, R> Field<ID, T>.collectWithSelf(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
    crossinline transform: (ID, T) -> R,
): List<R> = collectInternal(true, predicate, transform)

/**
 * Returns the list of field elements that satisfy the [predicate].
 * including the local value.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectWithSelf(
    crossinline predicate: (ID, T) -> Boolean = { _, _ -> true },
): List<T> = collectWithSelf(predicate) { _, value -> value }

/**
 * Check if the field contains the [value], **including the local value**.
 * If you need to exclude the local value, use instead:
 *
 * ```kotlin
 * value in field.withoutSelf().values
 * ```
 */
operator fun <ID : Any, T> Field<ID, T>.contains(value: T): Boolean = anyWithSelf { it == value }

/**
 * Check if the field contains the [targetId], **including the local id**.
 * If you need to exclude the local value, use instead:
 *
 * ```kotlin
 * id in field.withoutSelf().keys
 * ```
 */
fun <ID : Any, T> Field<ID, T>.containsId(targetId: ID): Boolean =
    foldWithId(localId == targetId) { current, id, _ -> current || id == targetId }

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
 * Returns the element yielding the largest value of the given [selector].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxBy(base: T, crossinline selector: (T) -> R): T =
    maxWith(base, compareBy(selector))

/**
 * Returns the element yielding the largest value of the given [comparator].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 */
fun <ID : Any, T> Field<ID, T>.maxWith(base: T, comparator: Comparator<T>): T = minWith(base, comparator.reversed())

/**
 * Returns the element yielding the smallest value of the given [comparator].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 */
fun <ID : Any, T> Field<ID, T>.minWith(base: T, comparator: Comparator<T>): T =
    fold(base) { acc, value -> if (comparator.compare(acc, value) < 0) acc else value }

/**
 * Returns the element yielding the smallest value of the given [comparator].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 */
fun <ID : Any, T> Field<ID, T>.minWithId(base: Pair<ID, T>, comparator: Comparator<Pair<ID, T>>): Pair<ID, T> =
    foldWithId(base) { acc, id, value -> minOf(acc, id to value, comparator) }

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
