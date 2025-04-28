/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry

/*
 * TODO: use context parameters, once available, to make this entity cleaner:
 *
 * context(field: Field<ID, T>)
 * inline fun <ID : Any, T, R> ReductionType.initWith(default: R, crossinline self: (ID, T) -> R): R = when (this) {
 *     IncludingSelf -> self(field.local.id, field.local.value)
 *     ExcludingSelf -> default
 * }
 */

/**
 * A marker interface representing whether the local value should be included in a reduction.
 */
sealed interface ReductionType

/**
 * A [ReductionType] that includes the local value in the reduction.
 */
data object IncludingSelf : ReductionType

/**
 * A [ReductionType] that excludes the local value from the reduction.
 */
data object ExcludingSelf : ReductionType

/**
 * Computes the initial value for a reduction, depending on whether the local value is included.
 *
 * @param field the field from which to extract the local ID and value.
 * @param default the value to use if the local value is excluded.
 * @param self a function that computes the initial value from the local ID and value.
 * @return the result of [self] if this is [IncludingSelf], or [default] if [ExcludingSelf].
 */
inline fun <ID : Any, T, R> ReductionType.init(field: Field<ID, T>, default: R, crossinline self: (ID, T) -> R): R =
    when (this) {
        IncludingSelf -> self(field.local.id, field.local.value)
        ExcludingSelf -> default
    }

/**
 * Computes the initial value for a reduction using a [FieldEntry], based on whether the local value is included.
 *
 * @param field the field from which to extract the local entry.
 * @param default the value to use if the local value is excluded.
 * @param self a function that computes the initial value from the local [FieldEntry].
 * @return the result of [self] if this is [IncludingSelf], or [default] if [ExcludingSelf].
 */
inline fun <ID : Any, T, R> ReductionType.init(
    field: Field<ID, T>,
    default: R,
    crossinline self: (FieldEntry<ID, T>) -> R,
): R = when (this) {
    IncludingSelf -> self(FieldEntry(field.local.id, field.local.value))
    ExcludingSelf -> default
}

/**
 * Selects the initial value for a reduction based on whether the local value is included.
 *
 * @param default the value to use if the local value is excluded.
 * @param self the value to use if the local value is included.
 * @return [self] if this is [IncludingSelf], or [default] if [ExcludingSelf].
 */
fun <R> ReductionType.initTo(default: R, self: R): R = when (this) {
    IncludingSelf -> self
    ExcludingSelf -> default
}
