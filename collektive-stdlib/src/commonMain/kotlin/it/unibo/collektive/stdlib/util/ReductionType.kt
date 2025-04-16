/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.aggregate.Field

/**
 * A type that indicates whether the local value should be included in the reduction or not.
 *
 * @param ID device identifier type
 * @param T the type of the value
 */
sealed interface ReductionType {

    /**
     * The initial value of the reduction.
     *
     * @param default the default value
     * @param self a function that takes the local ID and the local value and returns the initial value
     * @return the initial value of the reduction
     */
    context(_: Field<ID, T>)
    fun <ID: Any, T, R> initial(default: R, self: (ID, T) -> R): R

    /**
     * The initial value of the reduction.
     *
     * @param default the default value
     * @param self the local value
     * @return the initial value of the reduction
     */
    fun <R> initial(default: R, self: R): R
}

/**
 * A reduction type that includes the local value in the reduction.
 */
data object IncludingSelf : ReductionType {

    context(_: Field<ID, T>)
    override fun <ID : Any, T, R> initial(default: R, self: (ID, T) -> R): R = self(this.localId, field.localValue)

    override fun <R> initial(default: R, self: R): R  = self
}

/**
 * A reduction type that excludes the local value from the reduction.
 */
data object ExcludingSelf : ReductionType {
    context(field: Field<ID, T>)
    override fun <ID : Any, T, R> initial(default: R, self: (ID, T) -> R): R = default

    override fun <R> initial(default: R, self: R): R  = default
}
