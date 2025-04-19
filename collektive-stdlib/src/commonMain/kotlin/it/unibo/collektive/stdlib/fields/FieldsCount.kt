/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import arrow.core.Predicate
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import it.unibo.collektive.stdlib.util.initTo
import kotlin.jvm.JvmOverloads

/**
 * Counts the number of field entries that satisfy the given [predicate].
 *
 * By default, the local entry is excluded from the count.
 * It is included only if [reductionType] is [IncludingSelf].
 *
 * @param reductionType specifies whether to include the local entry in the count (default is [ExcludingSelf]).
 * @param predicate the condition to evaluate for each entry.
 * @return the number of entries that satisfy the predicate.
 */
@JvmOverloads
inline fun <ID : Any, T> Field<ID, T>.countMatching(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Int = fold(reductionType.initTo(0, 1)) { acc, value -> if (predicate(value)) acc + 1 else acc }

/**
 * Counts the number of field values that satisfy the given [predicate].
 *
 * By default, the local value is excluded from the count.
 * It is included only if [reductionType] is [IncludingSelf].
 *
 * @param reductionType specifies whether to include the local value in the count (default is [ExcludingSelf]).
 * @param predicate the condition to evaluate for each value.
 * @return the number of values that satisfy the predicate.
 */
@JvmOverloads
inline fun <T> Field<*, T>.countMatchingValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Int = countMatching(reductionType) { (_, value) -> predicate(value) }

/**
 * Counts the number of field IDs that satisfy the given [predicate].
 *
 * By default, the local ID is excluded from the count.
 * It is included only if [reductionType] is [IncludingSelf].
 *
 * @param reductionType specifies whether to include the local ID in the count (default is [ExcludingSelf]).
 * @param predicate the condition to evaluate for each ID.
 * @return the number of IDs that satisfy the predicate.
 */
@JvmOverloads
inline fun <ID : Any> Field<ID, *>.countMatchingIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Int = countMatching(reductionType) { (id, _) -> predicate(id) }
