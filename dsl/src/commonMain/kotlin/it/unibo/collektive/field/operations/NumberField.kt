@file:Suppress("TooManyFunctions")

package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.hood

/**
 * Adds [value] to all the field values.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.plus(value: T): Field<ID, T> = map { plus(it, value) }

/**
 * Subtracts [value] from all the field values.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.minus(value: T): Field<ID, T> = map { minus(it, value) }

/**
 * Multiplies all the field values by [value].
 */
operator fun <ID : Any, T : Number> Field<ID, T>.times(value: T): Field<ID, T> = map { times(it, value) }

/**
 * Divides all the field values by [value].
 */
operator fun <ID : Any, T : Number> Field<ID, T>.div(value: T): Field<ID, T> = map { div(it, value) }

/**
 * Computes the remainder of the division of all the field values by [value].
 */
operator fun <ID : Any, T : Number> Field<ID, T>.rem(value: T): Field<ID, T> = map { rem(it, value) }

/**
 * Sum a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.plus(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> plus(a, b) }

/**
 * Subtract a field with [other] field.
 * The two fields must be aligned, otherwise an error is thrown.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.minus(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> minus(a, b) }

/**
 * Multiply a field with [other] field.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.times(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> times(a, b) }

/**
 * Divide a field with [other] field.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.div(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> div(a, b) }

/**
 * Compute the remainder of the division of a field with [other] field.
 */
operator fun <ID : Any, T : Number> Field<ID, T>.rem(other: Field<ID, T>): Field<ID, T> =
    alignedMap(other) { a, b -> rem(a, b) }

/**
 * Sum all the neighbors of a field.
 * The [default] value is used if no neighbors are in the field.
 */
fun <ID : Any, T : Number> Field<ID, T>.sum(default: T): T = hood(default) { acc, value -> plus(acc, value) }
