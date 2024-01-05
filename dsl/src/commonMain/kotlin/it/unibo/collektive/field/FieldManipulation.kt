package it.unibo.collektive.field

import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.Field.Companion.reduce

/**
 * Get the minimum value of a field.
 * @param includingSelf if true the local node is included in the computation.
 */
fun <T : Comparable<T>> Field<T>.min(includingSelf: Boolean = true): T = when (includingSelf) {
    true -> hood { acc, value -> if (value < acc) value else acc }
    false -> reduce(includingSelf = false) { acc, value -> if (value < acc) value else acc }
}

/**
 * Get the maximum value of a field.
 * @param includingSelf if true the local node is included in the computation.
 */
fun <T : Comparable<T>> Field<T>.max(includingSelf: Boolean = true): T = when (includingSelf) {
    true -> hood { acc, value -> if (value > acc) value else acc }
    false -> reduce(includingSelf = false) { acc, value -> if (value > acc) value else acc }
}

/**
 * Operator to sum a [value] to all the values of the field.
 */
operator fun <T : Number> Field<T>.plus(value: T): Field<T> = mapWithId { _, oldValue -> add(oldValue, value) }

/**
 * Operator to subtract a [value] to all the values of the field.
 */
operator fun <T : Number> Field<T>.minus(value: T): Field<T> = mapWithId { _, oldValue -> sub(oldValue, value) }

/**
 * Sum a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <T : Number> Field<T>.plus(other: Field<T>): Field<T> = combine(other) { a, b -> add(a, b) }

/**
 * Subtract a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <T : Number> Field<T>.minus(other: Field<T>): Field<T> = combine(other) { a, b -> sub(a, b) }

/**
 * Combine two fields with a [transform] function.
 * The two fields must be aligned, otherwise an error is thrown.
 */
fun <T, V, R> Field<T>.combine(otherField: Field<V>, transform: (T, V) -> R): Field<R> {
    require(neighborsCount == otherField.neighborsCount) {
        """
            Field misalignment. This is most likely a bug in Collektive,
            please report at https://github.com/Collektive/collektive/issues/new/choose
            base field: $this
            field it has being combined with: $otherField
            The field has $neighborsCount neighbors, while the other field has ${otherField.neighborsCount} neighbors.
        """.trimIndent()
    }
    return mapWithId { id, value -> transform(value, otherField[id] ?: error("Field not aligned")) }
}

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
