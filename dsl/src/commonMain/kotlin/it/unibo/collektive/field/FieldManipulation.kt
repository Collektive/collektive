@file:Suppress("TooManyFunctions")

package it.unibo.collektive.field

import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.hood

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
fun <ID : Any, T : Comparable<T>> Field<ID, T>.maxWithSelf(): T = min(localValue)

/**
 * Adds [value] to all the field values.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.plus(value: T): Field<ID, T> = map { add(it, value) }

/**
 * Subtracts [value] from all the field values.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.minus(value: T): Field<ID, T> = map { sub(it, value) }

/**
 * Sum a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.plus(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> add(a, b) }

/**
 * Subtract a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.minus(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> sub(a, b) }

/**
 * Sum all the neighbors of a field.
 * The [default] value is used if no neighbors are in the field.
 */
fun <ID : Any, T : Number> Field<ID, T>.sum(default: T): T = hood(default) { acc, value -> add(acc, value) }

/**
 * Count the number of elements in the field that satisfy the [predicate].
 * The [default] value is included in the count if it satisfies the [predicate].
 * If the [default] value is not provided, the [predicate] is applied to the neighbors only.
 */
fun <ID : Any, T> Field<ID, T>.count(default: T? = null, predicate: (T) -> Boolean = { true }): Int =
    when {
        default == null -> fold(0) { acc, value -> if (predicate(value)) acc + 1 else acc }
        else -> fold(if (predicate(default)) 1 else 0) { acc, value -> if (predicate(value)) acc + 1 else acc }
    }

/**
 * Check if all the elements in the field satisfy the [predicate].
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any, T> Field<ID, T>.all(default: T, predicate: (T) -> Boolean): Boolean =
    fold(predicate(default)) { acc, value -> acc && predicate(value) }

/**
 * Check if all the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any> Field<ID, Boolean>.all(default: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(default)) { acc, value -> acc && predicate(value) }

/**
 * Check if any of the elements in the field satisfy the [predicate].
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any, T> Field<ID, T>.any(default: T, predicate: (T) -> Boolean): Boolean =
    fold(predicate(default)) { acc, value -> acc || predicate(value) }

/**
 * Check if any of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any> Field<ID, Boolean>.any(default: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(default)) { acc, value -> acc || predicate(value) }

/**
 * Check if none of the elements in the field satisfy the [predicate].
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any, T> Field<ID, T>.none(default: T, predicate: (T) -> Boolean): Boolean =
    !all(default, predicate)

/**
 * Check if none of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any> Field<ID, Boolean>.none(default: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    !all(default, predicate)

@Suppress("UNCHECKED_CAST")
private fun <T : Number> add(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() + other.toDouble()) as T
        is Float -> (value.toFloat() + other.toFloat()) as T
        is Long -> (value.toLong() + other.toLong()) as T
        is Int -> (value.toInt() + other.toInt()) as T
        is Short -> (value.toShort() + other.toShort()) as T
        is Byte -> (value.toByte() + other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}

@Suppress("UNCHECKED_CAST")
private fun <T : Number> sub(value: T, other: T): T {
    return when (value) {
        is Double -> (value.toDouble() - other.toDouble()) as T
        is Float -> (value.toFloat() - other.toFloat()) as T
        is Long -> (value.toLong() - other.toLong()) as T
        is Int -> (value.toInt() - other.toInt()) as T
        is Short -> (value.toShort() - other.toShort()) as T
        is Byte -> (value.toByte() - other.toByte()) as T
        else -> error("Unsupported type ${value::class.simpleName}")
    }
}
