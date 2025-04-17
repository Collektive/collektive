/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import arrow.core.Predicate
import arrow.core.identity
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import it.unibo.collektive.stdlib.util.init
import it.unibo.collektive.stdlib.util.initTo
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.jvm.JvmOverloads

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
 * Collects the field entries that satisfy the given [predicate], transforms them using [transform],
 * and stores the results in a new [Collection] produced by [produceAccumulator].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param produceAccumulator function to create the result collection with an initial capacity.
 * @param predicate the filter applied to each field entry.
 * @param transform the mapping function applied to each matching entry.
 * @return a mutable collection containing the transformed matching entries.
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
    if (reductionType.init(this, false, predicate)) {
        collected.add(transform(local))
    }
    fold(collected) { _, entry ->
        if (predicate(entry)) collected.add(transform(entry))
        collected
    }
    return collected
}

/**
 * Collects the field entries that satisfy the given [predicate], transforms them using [transform],
 * and stores the results in the provided [accumulator].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param accumulator the collection into which results are stored.
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @param transform the mapping function applied to each matching entry.
 * @return the same [accumulator] with the transformed entries added.
 */
@JvmOverloads
inline fun <ID : Any, T, R, C : MutableCollection<R>> Field<ID, T>.collectInto(
    accumulator: C,
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
    crossinline transform: (FieldEntry<ID, T>) -> R,
): C = collectInto(reductionType, { accumulator }, predicate, transform)

/**
 * Collects the field entries that satisfy the given [predicate], transforms them using [transform],
 * and returns the result as a [List].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @param transform the mapping function applied to each matching entry.
 * @return a list of transformed matching entries.
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
 * Collects the field entries that satisfy the given [predicate], transforms them using [transform],
 * and returns the result as a distinct [Set].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @param transform the mapping function applied to each matching entry.
 * @return a set of transformed matching entries.
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

/**
 * Collects the distinct field entries that satisfy the given [predicate].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @return a set of matching field entries.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Set<FieldEntry<ID, T>> = collectDistinctMatchingMapped(reductionType, predicate, ::identity)

/**
 * Collects the distinct field entries whose ID satisfies the given [predicate].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry's ID.
 * @return a set of matching field entries.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Set<FieldEntry<ID, T>> = collectDistinctMatchingMapped(reductionType, { predicate(it.id) }, ::identity)

/**
 * Collects the distinct field entries whose value satisfies the given [predicate].
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry's value.
 * @return a set of matching field entries.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Set<FieldEntry<ID, T>> = collectDistinctMatchingMapped(reductionType, { predicate(it.value) }, ::identity)

/**
 * Collects all distinct field values.
 *
 * By default, the local value is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local value (default is [ExcludingSelf]).
 * @return a set of all distinct values in the field.
 */
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.collectDistinctValues(reductionType: ReductionType = ExcludingSelf): Set<T> =
    collectDistinctValuesMatchingIDs(reductionType) { true }

/**
 * Collects the distinct values of entries whose IDs satisfy the given [predicate].
 *
 * By default, the local value is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local value (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry’s ID.
 * @return a set of distinct values whose IDs match the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctValuesMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Set<T> = collectDistinctMatchingMapped(reductionType, { predicate(it.id) }) { it.value }

/**
 * Collects the distinct values of entries whose values satisfy the given [predicate].
 *
 * By default, the local value is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local value (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry’s value.
 * @return a set of distinct values that satisfy the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectDistinctValuesMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Set<T> = collectDistinctMatchingMapped(reductionType, { predicate(it.value) }) { it.value }

/**
 * Collects the IDs of entries that satisfy the given [predicate].
 *
 * By default, the local ID is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local ID (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @return a set of IDs from entries that satisfy the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectIDsMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Set<ID> = collectDistinctMatchingMapped(reductionType, predicate) { it.id }

/**
 * Collects the IDs of entries whose values satisfy the given [predicate].
 *
 * By default, the local ID is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local ID (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry’s value.
 * @return a set of IDs from entries that satisfy the value predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectIDsMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Set<ID> = collectDistinctMatchingMapped(reductionType, { predicate(it.value) }) { it.id }

/**
 * Collects all entries that satisfy the given [predicate] into a list.
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @return a list of matching field entries.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): List<FieldEntry<ID, T>> = collectMatchingMapped(reductionType, predicate, ::identity)

/**
 * Collects all entries whose IDs satisfy the given [predicate] into a list.
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry’s ID.
 * @return a list of field entries whose IDs match the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): List<FieldEntry<ID, T>> = collectMatching(reductionType) { predicate(it.id) }

/**
 * Collects all entries whose values satisfy the given [predicate] into a list.
 *
 * By default, the local entry is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry’s value.
 * @return a list of field entries whose values match the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): List<FieldEntry<ID, T>> = collectMatching(reductionType) { predicate(it.value) }

/**
 * Collects the values of entries that satisfy the given [predicate] into a list.
 *
 * By default, the local value is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local value (default is [ExcludingSelf]).
 * @param predicate the filter applied to each field entry.
 * @return a list of values from matching entries.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectValuesMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): List<T> = collectMatchingMapped(reductionType, predicate) { it.value }

/**
 * Collects the values of entries whose IDs satisfy the given [predicate] into a list.
 *
 * By default, the local value is excluded from the result.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * @param reductionType specifies whether to include the local value (default is [ExcludingSelf]).
 * @param predicate the filter applied to each entry’s ID.
 * @return a list of values from entries whose IDs match the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.collectValuesMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): List<T> = collectMatchingMapped(reductionType, { predicate(it.id) }) { it.value }
