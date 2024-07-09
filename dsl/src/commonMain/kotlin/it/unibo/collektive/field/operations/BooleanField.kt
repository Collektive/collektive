package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold
import kotlin.jvm.JvmOverloads

/**
 * Check if all the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The [base] value is used as a first element to start the fold operation at which the [predicate] is applied.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.all(base: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(base)) { acc, value -> acc && predicate(value) }

/**
 * Check if any of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The [base] value is used as a first element to start the fold operation at which the [predicate] is applied.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.any(base: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(base)) { acc, value -> acc || predicate(value) }

/**
 * Check if none of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The [base] value is used as a first element to start the fold operation at which the [predicate] is applied.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.none(base: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    !all(base, predicate)
