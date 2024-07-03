package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import kotlin.jvm.JvmOverloads

/**
 * Count the number of elements in the field that satisfy the [predicate],
 * ignoring the local value.
 */
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.count(predicate: (T) -> Boolean = { true }): Int =
    fold(0) { acc, value -> if (predicate(value)) acc + 1 else acc }

/**
 * Count the number of elements in the field that satisfy the [predicate],
 * including the local value.
 */
@JvmOverloads
fun <ID : Any, T> Field<ID, T>.countWithSelf(predicate: (T) -> Boolean = { true }): Int =
    count(predicate) + if (predicate(localValue)) 1 else 0

/**
 * Check if all the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
fun <ID : Any, T> Field<ID, T>.all(predicate: (T) -> Boolean): Boolean =
    fold(true) { acc, value -> acc && predicate(value) }

/**
 * Check if all the elements in the field satisfy the [predicate],
 * including the local value.
 */
fun <ID : Any, T> Field<ID, T>.allWithSelf(predicate: (T) -> Boolean): Boolean =
    all(predicate) && predicate(localValue)

/**
 * Check if any of the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
fun <ID : Any, T> Field<ID, T>.any(predicate: (T) -> Boolean): Boolean =
    fold(false) { acc, value -> acc || predicate(value) }

/**
 * Check if any of the elements in the field satisfy the [predicate],
 * including the local value.
 */
fun <ID : Any, T> Field<ID, T>.anyWithSelf(predicate: (T) -> Boolean): Boolean =
    any(predicate) || predicate(localValue)

/**
 * Check if none of the elements in the field satisfy the [predicate],
 * ignoring the local value.
 */
fun <ID : Any, T> Field<ID, T>.none(predicate: (T) -> Boolean): Boolean =
    !all(predicate)

/**
 * Check if none of the elements in the field satisfy the [predicate],
 * including the local value.
 */
fun <ID : Any, T> Field<ID, T>.noneWithSelf(predicate: (T) -> Boolean): Boolean =
    !allWithSelf(predicate)
