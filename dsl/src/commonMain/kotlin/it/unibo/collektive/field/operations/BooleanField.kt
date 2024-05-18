package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import it.unibo.collektive.field.Field.Companion.fold

/**
 * Check if all the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any> Field<ID, Boolean>.all(default: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(default)) { acc, value -> acc && predicate(value) }

/**
 * Check if any of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any> Field<ID, Boolean>.any(default: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(default)) { acc, value -> acc || predicate(value) }

/**
 * Check if none of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The local value is not considered, unless explicitly passed as [default].
 */
fun <ID : Any> Field<ID, Boolean>.none(default: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    !all(default, predicate)
