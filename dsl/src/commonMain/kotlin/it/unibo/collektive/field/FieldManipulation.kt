package it.unibo.collektive.field

import it.unibo.collektive.ID

/**
 * Get the minimum value of a field.
 * @param includingSelf if true the local node is included in the computation.
 */
fun <T : Comparable<T>> Field<T>.min(includingSelf: Boolean = true): Map.Entry<ID, T>? =
    handle(includingSelf).minByOrNull { it.value }

/**
 * Get the maximum value of a field.
 * @param includingSelf if true the local node is included in the computation.
 */
fun <T : Comparable<T>> Field<T>.max(includingSelf: Boolean = true): Map.Entry<ID, T>? =
    handle(includingSelf).maxByOrNull { it.value }

/**
 * Manages the field to exclude the local node if [includingSelf] is false.
 */
private fun <T : Comparable<T>> Field<T>.handle(includingSelf: Boolean): Map<ID, T> =
    if (includingSelf) this.toMap() else this.excludeSelf()

/**
 * Operator to sum a [value] to all the values of the field.
 */
operator fun <T : Number> Field<T>.plus(value: T): Field<T> = map { add(it, value) }

/**
 * Operator to subtract a [value] to all the values of the field.
 */
operator fun <T : Number> Field<T>.minus(value: T): Field<T> = map { sub(it, value) }

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
