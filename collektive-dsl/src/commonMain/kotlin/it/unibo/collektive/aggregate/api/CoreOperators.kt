/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api

import it.unibo.collektive.aggregate.api.Aggregate.Companion.dataSharingMethod
import it.unibo.collektive.aggregate.api.Aggregate.InternalAPI
import it.unibo.collektive.field.Field

/**
 * [exchange] implements *anisotropic* data sharing
 * (the field information looks different depending on the direction from which you are observing it).
 *
 * Starting from an [initial] value, it computes a [Field] of [Shared] values
 * containing the local value and the values of all neighbors.
 *
 * It computes a [body] function based on such [Field],
 * and expects another [Field] of [Shared] as a result.
 *
 * The resulting [Field] is then used to send custom messages to known neighbors,
 * with their [Field] value used as message content.
 *
 * Then, the [body] must return a [Returned] by calling [YieldingContext.yielding] on the [Shared] [Field].
 *
 * Results are [Aggregate.evolve]d across rounds,
 * namely, this function also stores the local value of the field as state,
 * and later invocations in successive rounds will not start from the provided [initial] value,
 * rather, from the previously computed local field value.
 */
inline fun <ID : Any, reified Shared, Returned> Aggregate<ID>.exchanging(
    initial: Shared,
    noinline body: YieldingScope<Field<ID, Shared>, Returned>,
): Returned =
    @OptIn(DelicateCollektiveApi::class)
    InternalAPI.`_ serialization aware exchanging`(initial, dataSharingMethod(), body)

/**
 * Observes the value of an expression [local] across neighbors.
 *
 * ## Example
 *
 * ```kotlin
 * val field = neighboring(0)
 * ```
 *
 * The field returned has as local value the value passed as input (0 in this example).
 *
 * ```kotlin
 * val field = neighboring({ 2 * 2 })
 * ```
 *
 * In this case, the field returned has the computation as a result,
 * in form of a field of functions with type `() -> Int`.
 */
inline fun <ID : Any, reified Shared> Aggregate<ID>.neighboring(local: Shared): Field<ID, Shared> =
    @OptIn(DelicateCollektiveApi::class)
    InternalAPI.`_ serialization aware neighboring`(local, dataSharingMethod())
