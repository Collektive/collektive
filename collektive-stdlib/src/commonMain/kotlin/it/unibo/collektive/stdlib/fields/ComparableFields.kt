/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.Field.Companion.fold
import it.unibo.collektive.aggregate.FieldEntry

/**
 * Get the minimum value of a field, excluding the local value, starting from [base].
 * To consider the local value, explicitly provide it as [base].
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.min(base: T): T =
    fold(base) { acc, value -> if (value < acc) value else acc }

/**
 * Get the minimum value of a field, including the local value.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.minWithSelf(): T = min(localValue)

/**
 * Get the maximum value of a field, excluding the local value, starting from [base].
 * To consider the local value, explicitly provide it as [base].
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.max(base: T): T =
    fold(base) { acc, value -> if (value > acc) value else acc }

/**
 * Returns the element yielding the largest value of the given [selector],
 * excluding the local value.
 * In case multiple elements are maximal, there is no guarantee which one will be returned.
 * If the field contains only the local value, the result is `null`.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.maxByValue(): FieldEntry<ID, T>? = maxBy { it.value }

/**
 * Get the maximum value of a field, including the local value.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.maxWithSelf(): T = max(localValue)
