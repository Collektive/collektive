package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold

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
 * Get the maximum value of a field, including the local value.
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.maxWithSelf(): T = max(localValue)
