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
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import kotlin.jvm.JvmOverloads

/**
 * Returns the entry with the maximum value in the field.
 *
 * By default, the local entry is excluded from the comparison.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * If the field contains no applicable entries, the result is `null`.
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @return the entry with the highest value, or `null` if no neighbors exist.
 */
@JvmOverloads
fun <ID : Any, T : Comparable<T>> Field<ID, T>.max(reductionType: ReductionType = ExcludingSelf): FieldEntry<ID, T>? =
    maxBy(reductionType) { it.value }

//TODO MAX OR NULL
//TODO MIN OR NULL
// min by { it > Zero }

/**
 * Returns the maximum value in the field,
 * excluding the local value.
 *
 * If the field contains only the local entry, the result is `null`.
 *
 * @return the highest value in the field, or `null` if no neighbors exist.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.maxValue(): T? = max()?.value

/**
 * Returns the maximum between [base] and the maximum value found in the field.
 *
 * The [base] value is always considered in the comparison.
 * The field-based maximum excludes the local value.
 *
 * @param base the reference value to compare against the maximum field value.
 * @return the greater of [base] and the maximum field value, or [base] if no neighbors exist.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.maxValue(base: T): T = maxOf(base, maxValue() ?: base)

/**
 * Returns the entry with the minimum value in the field.
 *
 * By default, the local entry is excluded from the comparison.
 * It is included only if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf].
 *
 * If the field contains no applicable entries, the result is `null`.
 *
 * @param reductionType specifies whether to include the local entry (default is [ExcludingSelf]).
 * @return the entry with the lowest value, or `null` if no neighbors exist.
 */
@JvmOverloads
fun <ID : Any, T : Comparable<T>> Field<ID, T>.min(reductionType: ReductionType = ExcludingSelf): FieldEntry<ID, T>? =
    minBy(reductionType) { it.value }

/**
 * Returns the minimum value in the field,
 * excluding the local value.
 *
 * If the field contains only the local entry, the result is `null`.
 *
 * @return the lowest value in the field, or `null` if no neighbors exist.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.minValue(): T? = min()?.value

/**
 * Returns the minimum between [base] and the minimum value found in the field.
 *
 * The [base] value is always considered in the comparison.
 * The field-based minimum excludes the local value.
 *
 * @param base the reference value to compare against the minimum field value.
 * @return the lesser of [base] and the minimum field value, or [base] if no neighbors exist.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.minValue(base: T): T = minOf(base, minValue() ?: base)
