package it.unibo.collektive.field

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
