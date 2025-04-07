/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import kotlinx.serialization.Serializable
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * A serializable [Option] type that can be either [Some] or [None].
 */
@Serializable
sealed interface Option<out T> {

    /**
     * Returns the value if this is a [Some] and [predicate] is true, or null otherwise.
     */
    fun filter(predicate: (T) -> Boolean): Option<T> = when (this) {
        is Some -> if (predicate(value)) this else None
        None -> this
    }

    /**
     * An empty [Option].
     */
    @Serializable object None : Option<Nothing> {
        override fun toString() = "None"
    }

    /**
     * An [Option] containing a [value].
     */
    @Serializable data class Some<T>(val value: T) : Option<T>

    /**
     * Extensions requiring to work around covariance.
     */
    companion object {

        /**
         * Returns an [Option] combining the current and the provided one.
         * If any is [None], then the other one is returned.
         * Otherwise, the [combiner] function is called with the values of both options.
         */
        @OptIn(ExperimentalContracts::class)
        inline fun <T> Option<T>.merge(other: Option<T>, combiner: (T, T) -> T): Option<T> {
            contract { callsInPlace(combiner, InvocationKind.AT_MOST_ONCE) }
            return when {
                this is Some && other is Some -> Some(combiner(value, other.value))
                this is None -> other
                other is None -> this
                else -> error("This is not possible")
            }
        }
    }
}
