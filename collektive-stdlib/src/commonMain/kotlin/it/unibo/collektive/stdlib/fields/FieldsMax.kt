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
import it.unibo.collektive.aggregate.api.PurelyLocal
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.IncludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import kotlin.jvm.JvmOverloads

/**
 * Returns the entry that yields the largest value according to the given [selector].
 *
 * By default, the local entry is excluded from the comparison.
 * It is included only if [reductionType] is [IncludingSelf].
 *
 * If multiple entries produce the same maximal value, one is returned arbitrarily.
 * If the field contains no applicable entries, the result is `null`.
 *
 * @param reductionType specifies whether to include the local entry in the comparison (default is [ExcludingSelf]).
 * @param selector a function that maps each entry to a comparable value.
 * @return the entry with the highest value as determined by [selector], or `null` if none.
 */
@JvmOverloads
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxBy(
    reductionType: ReductionType = ExcludingSelf,
    crossinline selector: (FieldEntry<ID, T>) -> R,
): FieldEntry<ID, T>? = maxWith(reductionType, compareBy(selector))

/**
 * Returns the ID of the entry that yields the largest value according to the given [selector].
 *
 * @param reductionType specifies whether to include the local entry in the comparison (default is [ExcludingSelf]).
 * @param selector a function that maps each entry to a comparable value.
 * @return the ID of the entry with the highest value, or `null` if none.
 */
@JvmOverloads
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxIDBy(
    reductionType: ReductionType = ExcludingSelf,
    crossinline selector: (FieldEntry<ID, T>) -> R,
): ID? = maxBy(reductionType, selector)?.id

/**
 * Returns the value of the entry that yields the largest value according to the given [selector].
 *
 * @param reductionType specifies whether to include the local entry in the comparison (default is [ExcludingSelf]).
 * @param selector a function that maps each entry to a comparable value.
 * @return the value of the entry with the highest score, or `null` if none.
 */
@JvmOverloads
inline fun <ID : Any, T, R : Comparable<R>> Field<ID, T>.maxValueBy(
    reductionType: ReductionType = ExcludingSelf,
    crossinline selector: (FieldEntry<ID, T>) -> R,
): T? = maxBy(reductionType, selector)?.value

/**
 * Returns the entry that yields the largest value according to the given [comparator].
 *
 * By default, the local entry is excluded from the comparison.
 * It is included only if [reductionType] is [IncludingSelf].
 *
 * If multiple entries are considered equal under the comparator, one of them is returned arbitrarily.
 * If the field contains no applicable entries, the result is `null`.
 *
 * @param reductionType specifies whether to include the local entry in the comparison (default is [ExcludingSelf]).
 * @param comparator the comparator used to determine ordering between entries.
 * @return the entry with the greatest value, or `null` if none.
 */
@PurelyLocal
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.maxWith(
    reductionType: ReductionType = ExcludingSelf,
    comparator: Comparator<FieldEntry<ID, T>>,
): FieldEntry<ID, T>? = when (reductionType) {
    ExcludingSelf -> reduce { a, b -> maxOf(a, b, comparator) }
    IncludingSelf -> fold(local) { a, b -> maxOf(a, b, comparator) }
}

/**
 * Returns the value of the entry that yields the largest value according to the given [comparator].
 *
 * @param reductionType specifies whether to include the local entry in the comparison (default is [ExcludingSelf]).
 * @param comparator the comparator used to determine ordering between entries.
 * @return the value of the entry with the greatest ordering, or `null` if none.
 */
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.maxValueWith(
    reductionType: ReductionType = ExcludingSelf,
    comparator: Comparator<FieldEntry<ID, T>>,
): T? = maxWith(reductionType, comparator)?.value

/**
 * Returns the ID of the entry that yields the largest value according to the given [comparator].
 *
 * @param reductionType specifies whether to include the local entry in the comparison (default is [ExcludingSelf]).
 * @param comparator the comparator used to determine ordering between entries.
 * @return the ID of the entry with the greatest ordering, or `null` if none.
 */
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.maxIDWith(
    reductionType: ReductionType = ExcludingSelf,
    comparator: Comparator<FieldEntry<ID, T>>,
): ID? = maxWith(reductionType, comparator)?.id
