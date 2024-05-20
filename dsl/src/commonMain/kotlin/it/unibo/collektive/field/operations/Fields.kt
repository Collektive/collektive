package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold

/**
 * Count the number of elements in the field that satisfy the [predicate].
 * The [base] value is included in the count if it satisfies the [predicate].
 * If the [base] value is not provided, the [predicate] is applied to the neighbors only.
 */
fun <ID : Any, T> Field<ID, T>.count(base: T? = null, predicate: (T) -> Boolean = { true }): Int =
    when {
        base == null -> fold(0) { acc, value -> if (predicate(value)) acc + 1 else acc }
        else -> fold(if (predicate(base)) 1 else 0) { acc, value -> if (predicate(value)) acc + 1 else acc }
    }

/**
 * Check if all the elements in the field satisfy the [predicate].
 * The local value is not considered, unless explicitly passed as [base].
 */
fun <ID : Any, T> Field<ID, T>.all(base: T, predicate: (T) -> Boolean): Boolean =
    fold(predicate(base)) { acc, value -> acc && predicate(value) }

/**
 * Check if any of the elements in the field satisfy the [predicate].
 * The local value is not considered, unless explicitly passed as [base].
 */
fun <ID : Any, T> Field<ID, T>.any(base: T, predicate: (T) -> Boolean): Boolean =
    fold(predicate(base)) { acc, value -> acc || predicate(value) }

/**
 * Check if none of the elements in the field satisfy the [predicate].
 * The local value is not considered, unless explicitly passed as [base].
 */
fun <ID : Any, T> Field<ID, T>.none(base: T, predicate: (T) -> Boolean): Boolean =
    !all(base, predicate)
