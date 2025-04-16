/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:Suppress("TooManyFunctions")

package it.unibo.collektive.stdlib.fields

import arrow.core.Predicate
import arrow.core.fold
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.stdlib.util.Accumulator
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.FieldEntry
import it.unibo.collektive.stdlib.util.IncludingSelf
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.ReductionType
import it.unibo.collektive.stdlib.util.initTo
import it.unibo.collektive.stdlib.util.local
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

/**
 * Check if all the elements in the field satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any, T> Field<ID, T>.all(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = fold(reductionType.initTo(true, predicate)) { acc, value -> acc && predicate(value) }

/**
 * Check if any of the elements in the field satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any, T> Field<ID, T>.any(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = fold(reductionType.initTo(false, predicate)) { acc, value -> acc || predicate(value) }

/**
 * Returns the [Collection] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * ignoring the local value.
 */
@OptIn(ExperimentalContracts::class)
@PublishedApi
internal inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.genericCollect(
    produceAccumulator: (Int) -> C,
    reductionType: ReductionType,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): C {
    contract {
        callsInPlace(produceAccumulator, InvocationKind.EXACTLY_ONCE)
    }
    val collected = produceAccumulator(neighborsCount + reductionType.initTo(0, 1))
    if (reductionType.initTo(false, predicate)) {
        collected.add(transform(local()))
    }
    fold(collected) { _, entry ->
        if (predicate(entry)) collected.add(transform(entry))
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
    reductionType: ReductionType,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): List<R> = genericCollect(
    produceAccumulator = { ArrayList(it) },
    reductionType = reductionType,
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
    reductionType: ReductionType,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): Set<R> = genericCollect(
    produceAccumulator = { mutableSetOf() },
    reductionType = reductionType,
    predicate = predicate,
    transform = transform,
)


// TODO DAnilo continue here


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
operator fun <ID : Any, T> Field<ID, T>.contains(value: T): Boolean = any(IncludingSelf) { it == value }

/**
 * Check if the field contains the [targetId], **including the local id**.
 * If you need to exclude the local value, use instead:
 *
 * ```kotlin
 * id in field.withoutSelf().keys
 * ```
 */
fun <ID : Any, T> Field<ID, T>.containsId(targetId: ID): Boolean = targetId == localId || targetId in excludeSelf().keys

/**
 * Counts the number of elements in the field that satisfy the [predicate],
 * cosidering the local value if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [it.unibo.collektive.stdlib.util.ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.countMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Int = fold(reductionType.initTo(0, 1)) { acc, value -> if (predicate(value)) acc + 1 else acc }

/**
 * Counts the number of elements in the field that satisfy the [predicate],
 *  * cosidering the local value if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 *  * but defaulting to [it.unibo.collektive.stdlib.util.ExcludingSelf].
 */
@JvmOverloads
inline fun <T> Field<*, T>.countMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Int = countMatching(reductionType) { (_, value) -> predicate(value) }

/**
 * Counts the number of ids in the field that satisfy the [predicate],,
 *  * cosidering the local value if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 *  * but defaulting to [it.unibo.collektive.stdlib.util.ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any> Field<ID, *>.countMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Int = countMatching(reductionType) { (id, _) -> predicate(id) }

/**
 * Starting from the [initial] value, applies the [accumulator] function to each element in the field,
 * excluding the local value.
 */
@JvmOverloads
inline fun <ID : Any, T, R> Field<ID, T>.fold(
    initial: R,
    crossinline accumulator: Accumulator<R, FieldEntry<ID, T>>,
): R = excludeSelf().fold(initial) { current, (id, value) -> accumulator(current, FieldEntry(id, value)) }

/**
 * Starting from the [initial] value, applies the [accumulator] function to each id in the field,
 * excluding the local value.
 */
@JvmOverloads
inline fun <ID : Any, R> Field<ID, *>.foldIDs(initial: R, crossinline accumulator: Accumulator<R, ID>): R =
    fold(initial) { current, (id, _) -> accumulator(current, id) }

/**
 * Starting from the [initial] value, applies the [accumulator] function to each element in the field,
 * excluding the local value.
 */
@JvmOverloads
inline fun <T, R> Field<*, T>.foldValues(initial: R, crossinline accumulator: Accumulator<R, T>): R =
    fold(initial) { current, (_, value) -> accumulator(current, value) }

/**
 * Returns the element yielding the largest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): FieldEntry<ID, T>? = reduce { a, b -> maxOf(a, b, compareBy(selector)) }

/**
 * Returns the id yielding the largest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxIDBy(crossinline selector: (FieldEntry<ID, T>) -> R): ID? =
    maxBy(selector)?.id

/**
 * Returns the value yielding the largest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxValueBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): T? = maxBy(selector)?.value

/**
 * Returns the element yielding the largest value of the given [comparator].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T> Field<ID, T>.maxWith(comparator: Comparator<FieldEntry<ID, T>>): FieldEntry<ID, T>? =
    reduce { a, b -> maxOf(a, b, comparator) }

/**
 * Returns the element yielding the largest value of the given [comparator].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T> Field<ID, T>.maxValueWith(comparator: Comparator<FieldEntry<ID, T>>): T? = maxWith(comparator)?.value

/**
 * Returns the ID of the element yielding the largest value of the given [comparator].
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T> Field<ID, T>.maxIDWith(comparator: Comparator<FieldEntry<ID, T>>): ID? = maxWith(comparator)?.id

/**
 * Returns the element yielding the smallest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): FieldEntry<ID, T>? = reduce { a, b -> minOf(a, b, compareBy(selector)) }

/**
 * Returns the id of the element yielding the smallest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minIDBy(crossinline selector: (FieldEntry<ID, T>) -> R): ID? =
    minBy(selector)?.id

/**
 * Returns the value yielding the smallest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.minValueBy(
    crossinline selector: (FieldEntry<ID, T>) -> R,
): T? = minBy(selector)?.value

/**
 * Returns the element yielding the smallest value of the given [comparator].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T> Field<ID, T>.minWith(comparator: Comparator<FieldEntry<ID, T>>): FieldEntry<ID, T>? =
    maxWith(comparator.reversed())

/**
 * Returns the element yielding the smallest value of the given [comparator].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T> Field<ID, T>.minValueWith(comparator: Comparator<FieldEntry<ID, T>>): T? = minWith(comparator)?.value

/**
 * Returns the ID of the element yielding the smallest value of the given [comparator].
 * In case multiple elements are minimal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T> Field<ID, T>.minIDWith(comparator: Comparator<FieldEntry<ID, T>>): ID? = minWith(comparator)?.id

/**
 * Check if none of the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
inline fun <ID : Any, T> Field<ID, T>.none(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = !any(reductionType, predicate)

/**
 * Reduces the field entries to a single value using the provided [reducer] function,
 * _excluding the local value_.
 * If the field is empty, the result is `null`.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.reduce(crossinline reducer: Reducer<FieldEntry<ID, T>>): FieldEntry<ID, T>? =
    excludeSelf().entries.asSequence().map(::FieldEntry).reduceOrNull(reducer)

/**
 * Reduces the field ids to a single value using the provided [reducer] function,
 * _excluding the local value_.
 * If the field is empty, the result is `null`.
 */
@JvmOverloads
inline fun <ID : Any> Field<ID, *>.reduceIDs(crossinline reducer: Reducer<ID>): ID? =
    excludeSelf().keys.reduceOrNull(reducer)

/**
 * Reduces the field values to a single value using the provided [reducer] function,
 * _excluding the local value_.
 * If the field is empty, the result is `null`.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.reduceValues(crossinline reducer: Reducer<T>): T? =
    excludeSelf().values.reduceOrNull(reducer)

/**
 * Returns a new field containing [replacement] for each element that satisfies the [predicate].
 */
inline fun <ID : Any, T> Field<ID, T>.replaceMatchingValues(
    replacement: T,
    crossinline predicate: Predicate<T>,
): Field<ID, T> = replaceMatching(replacement, { (_, value) -> predicate(value) })

/**
 * Returns a new field containing [replacement] for each element that satisfies the [predicate].
 */
inline fun <ID : Any, T> Field<ID, T>.replaceMatching(
    replacement: T,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Field<ID, T> = mapWithId { id, value -> if (predicate(FieldEntry(id, value))) replacement else value }
