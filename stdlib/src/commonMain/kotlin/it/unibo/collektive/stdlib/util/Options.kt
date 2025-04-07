package it.unibo.collektive.stdlib.util

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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
