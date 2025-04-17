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
import arrow.core.identity
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.aggregate.toFieldEntry
import it.unibo.collektive.stdlib.util.Accumulator
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.IncludingSelf
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.ReductionType
import it.unibo.collektive.stdlib.util.initTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

/**
 * Checks if all the elements in the field satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any, T> Field<ID, T>.all(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = reductionType.initTo(true, predicate) && excludeSelf().all { predicate(it.toFieldEntry()) }

/**
 * Checks if all the values in the field satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <T> Field<*, T>.allValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Boolean = reductionType.initTo(true) { predicate(local.value) } && neighborsValues.all(predicate)

/**
 * Checks if all the IDs in the field satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any> Field<ID, *>.allIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Boolean = reductionType.initTo(true) { predicate(local.id) } && neighbors.all(predicate)

/**
 * Checks if any of the elements in the field satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any, T> Field<ID, T>.any(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = reductionType.initTo(false, predicate) || excludeSelf().any { predicate(it.toFieldEntry()) }

/**
 * Checks if any of the values in the field satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <T> Field<*, T>.anyValue(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Boolean = reductionType.initTo(false) { predicate(it.value) } || neighborsValues.any(predicate)

/**
 * Checks if any of IDs in the field satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any> Field<ID, *>.anyID(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Boolean = reductionType.initTo(false) { predicate(it.id) } || neighbors.any(predicate)

/**
 * Returns the [Collection] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
@OptIn(ExperimentalContracts::class)
inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.collectInto(
    reductionType: ReductionType = ExcludingSelf,
    crossinline produceAccumulator: (Int) -> C,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): C {
    contract {
        callsInPlace(produceAccumulator, InvocationKind.EXACTLY_ONCE)
    }
    val collected = produceAccumulator(neighborsCount + reductionType.initTo(0, 1))
    if (reductionType.initTo(false, predicate)) {
        collected.add(transform(local))
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
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.collectInto(
    accumulator: C,
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): C = collectInto(reductionType, { accumulator }, predicate, transform)

/**
 * Returns the [Collection] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.collectMatchingMapped(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): List<R> = collectInto(
    produceAccumulator = { ArrayList(it) },
    reductionType = reductionType,
    predicate = predicate,
    transform = transform,
)

/**
 * Returns the [List] of field elements that satisfy the [predicate],
 * transformed using the [transform] function.
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T, R> Field<ID, T>.collectDistinctMatchingMapped(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): Set<R> = collectInto(
    mutableSetOf(),
    reductionType = reductionType,
    predicate = predicate,
    transform = transform,
)

/*
 * Schema: collect[Distinct|IDs|DistinctValues][Matching[IDs|Values]]
 * All expected functions sorted lexicographically:
 * x collect (use field.entries)
 * - collectDistinctMatching
 * - collectDistinctMatchingIDs
 * - collectDistinctMatchingValues
 * - collectDistinctValues
 * - collectDistinctValuesMatchingIDs
 * - collectDistinctValuesMatchingValues
 * x collectIDs (use field.keys)
 * - collectIDsMatching
 * - collectIDsMatchingValues
 * x collectIDsMatchingIDs (field.keys.filter)
 * - collectMatching
 * - collectMatchingIDs
 * - collectMatchingValues
 * x collectValues (use field.values)
 * - collectValuesMatching
 * - collectValuesMatchingIDs
 * x collectValuesMatchingValues (field.values.filter)
 */

/**
 * Returns the [Set] of field elements that satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Set<FieldEntry<ID, T>> = collectDistinctMatchingMapped(reductionType, predicate, ::identity)

/**
 * Returns the [Set] of field elements whose ID satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Set<FieldEntry<ID, T>> = collectDistinctMatchingMapped(reductionType, { predicate(it.id) }, ::identity)

/**
 * Returns the [Set] of field elements whose values satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Set<FieldEntry<ID, T>> = collectDistinctMatchingMapped(reductionType, { predicate(it.value) }, ::identity)

/**
 * Returns the [Set] of field values.
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.collectDistinctValues(
    reductionType: ReductionType = ExcludingSelf,
): Set<T> = collectDistinctValuesMatchingIDs { true }

/**
 * Returns the [Set] of field values whose entry ID matches the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctValuesMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Set<T> = collectDistinctMatchingMapped(reductionType, { predicate(it.id) }) { it.value }

/**
 * Returns the [Set] of field values whose entry ID matches the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctValuesMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Set<T> = collectDistinctMatchingMapped(reductionType, { predicate(it.value) }) { it.value }

/**
 * Returns the IDs of the field whose entry satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectIDsMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Set<ID> = collectDistinctMatchingMapped(reductionType, predicate) { it.id }

/**
 * Returns the IDs of the field whose entry satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectIDsMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Set<ID> = collectDistinctMatchingMapped(reductionType, { predicate(it.value) }) { it.id }

/**
 * Returns the list of field elements that satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): List<FieldEntry<ID, T>> = collectMatchingMapped(reductionType, predicate, ::identity)


/**
 * Returns the list of field elements whose IDs satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): List<FieldEntry<ID, T>> = collectMatching(reductionType) { predicate(it.id) }

/**
 * Returns the list of field elements whose values satisfy the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): List<FieldEntry<ID, T>> = collectMatching(reductionType) { predicate(it.value) }

/**
 * Returns the values of the field whose entry satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectValuesMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): List<T> = collectMatchingMapped(reductionType, predicate) { it.value }

/**
 * Returns the values of the field whose entry satisfies the [predicate].
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf],
 * but defaulting to [ExcludingSelf].
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectValuesMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): List<T> = collectMatchingMapped(reductionType, { predicate(it.id) }) { it.value }

/**
 * Check if the field contains the [entry], **including [Field.local]**.
 * If you need to exclude the local value, use instead:
 *
 * ```kotlin
 * value in field.withoutSelf().values
 * ```
 */
operator fun <ID : Any, T> Field<ID, T>.contains(entry: FieldEntry<ID, T>): Boolean = any(IncludingSelf) { it == entry }

/**
 * Check if the field contains the [value], **including [Field.local]**.
 * If you need to exclude the local value, use instead:
 *
 * ```kotlin
 * value in field.withoutSelf().values
 * ```
 */
operator fun <T> Field<*, T>.contains(value: T): Boolean = anyValue(IncludingSelf) { it == value }

/**
 * Check if the field contains the [id], **including [Field.local]**.
 * If you need to exclude the local value, use instead:
 *
 * ```kotlin
 * value in field.withoutSelf().values
 * ```
 */
operator fun <ID: Any> Field<ID, *>.contains(id: ID): Boolean = anyID(IncludingSelf) { it == id }

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
): Field<ID, T> = map { (id, value) -> if (predicate(FieldEntry(id, value))) replacement else value }
