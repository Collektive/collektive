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

/**
 * A type that indicates whether the local value should be included in the reduction or not.
 *
 * @param ID device identifier type
 * @param T the type of the value
 */
sealed interface ReductionType

/**
 * A reduction type that includes the local value in the reduction.
 */
data object IncludingSelf : ReductionType

/**
 * A reduction type that excludes the local value from the reduction.
 */
data object ExcludingSelf : ReductionType

context(field: Field<ID, T>)
/**
 * The initial value of the reduction.
 *
 * @param default the default value
 * @param self a function that takes the local ID and the local value and returns the initial value
 * @return the initial value of the reduction
 */
inline fun <ID : Any, T, R> ReductionType.initTo(default: R, crossinline self: (ID, T) -> R): R = when (this) {
    IncludingSelf -> self(field.localId, field.localValue)
    ExcludingSelf -> default
}

context(field: Field<ID, T>)
/**
 * The initial value of the reduction.
 *
 * @param default the default value
 * @param self a function that takes the local ID and the local value and returns the initial value
 * @return the initial value of the reduction
 */
inline fun <ID : Any, T, R> ReductionType.initTo(default: R, crossinline self: (FieldEntry<ID, T>) -> R): R =
    when (this) {
        IncludingSelf -> self(FieldEntry(field.localId, field.localValue))
        ExcludingSelf -> default
    }

/**
 * The initial value of the reduction.
 *
 * @param default the default value
 * @param self the local value
 * @return the initial value of the reduction
 */
fun <R> ReductionType.initTo(default: R, self: R): R = when (this) {
    IncludingSelf -> self
    ExcludingSelf -> default
}
