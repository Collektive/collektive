@file:UseSerializers(OptionSerializer::class)

package it.unibo.collektive.stdlib.util

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.serialization.OptionSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.jvm.JvmInline

/**
 * A [Serializable] wrapper for [Option] to allow serialization of optional values.
 *
 * @param option The [Option] value to be serialized.
 */
@JvmInline
@Serializable
value class Maybe<out T> private constructor(val option: Option<T>) {

    /**
     * Support extension functions.
     */
    companion object {

        /**
         * The [Maybe] with no value.
         */
        val none: Maybe<Nothing> = Maybe(None)

        /**
         * Creates a [Maybe] from an existing [value].
         */
        fun <T> some(value: T): Maybe<T> = Maybe(Some(value))

        /**
         * Creates a [Maybe] from an [Option].
         *
         * @return A [Maybe] wrapping the provided [Option].
         */
        fun <T> Option<T>.serializable(): Maybe<T> = Maybe(this)

        /**
         * Merges two [Option]s into one. If both are [Some], the [combiner] function is applied to their values.
         * If one of them is [None], the other is returned.
         */
        @OptIn(ExperimentalContracts::class)
        inline fun <T> Option<T>.merge(other: Option<T>, crossinline combiner: (T, T) -> T): Option<T> {
            contract {
                callsInPlace(combiner, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
            }
            return when {
                this is Some && other is Some -> Some(combiner(value, other.value))
                isNone() -> other
                else -> this
            }
        }

        /**
         * Merges two [Maybe]s into one. If both are [Some], the [combiner] function is applied to their values.
         * If one of them is [None], the other is returned.
         */
        inline fun <T> Maybe<T>.merge(other: Maybe<T>, crossinline combiner: (T, T) -> T): Maybe<T> =
            option.merge(other.option, combiner).serializable()
    }
}
