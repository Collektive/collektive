package it.unibo.collektive.field

import it.unibo.collektive.field.Field.Companion.fold

/**
 * Get the minimum value of a field.
 * If [base] is unspecified, the local field value is used as base.
 */
fun <ID: Any, T : Comparable<T>> Field<ID, T>.min(base: T = localValue): T =
    fold(base) { acc, value -> if (value < acc) value else acc }

fun <ID: Any, T : Comparable<T>> Field<ID, T>.minHood(default: T = localValue): T = TODO()

/**
 * Get the maximum value of a field.
 * If [base] is unspecified, the local field value is used as base.
 */
fun <ID: Any, T : Comparable<T>> Field<ID, T>.max(base: T = localValue): T =
    fold(base) { acc, value -> if (value > acc) value else acc }

/**
 * Adds [value] to all the field values.
 */
operator fun <ID: Any, T : Number> Field<ID, T>.plus(value: T): Field<ID, T> = map { add(it, value) }

/**
 * Subtracts [value] from all the field values.
 */
operator fun <ID: Any, T : Number> Field<ID, T>.minus(value: T): Field<ID, T> = map { sub(it, value) }

/**
 * Sum a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <ID: Any, T : Number> Field<ID, T>.plus(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> add(a, b) }

/**
 * Subtract a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.minus(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> sub(a, b) }

///**
// * Combine two fields with a [transform] function.
// * The two fields must be aligned, otherwise an error is thrown.
// */
//fun <ID: Any, Type1, Type2, Result> combine(
//    field1: Field<ID, Type1>,
//    field2: Field<ID, Type2>,
//    transform: (Type1, Type2) -> Result
//): Field<ID, Result> {
//    // TODO: we should have a function producing a clear error every time there is a misalignment bug
//    field1.mapWithId { id, value -> transform(value, field2[id] ?: error("Field not aligned")) }
//}

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
