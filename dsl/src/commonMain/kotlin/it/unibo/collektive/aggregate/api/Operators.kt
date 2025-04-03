/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api

import it.unibo.collektive.field.Field

/**
 * [exchange] implements *anisotropic* data sharing
 * (namely, the field information looks different depending on the direction from which you are observing it).
 *
 * Namely, starting from an [initial] value, it computes a [Field] of [Shared] values
 * containing the local value and the values of all neighbors.
 *
 * It computes a [body] function based on such [Field],
 * and expects another [Field] as a result.
 *
 * The resulting [Field] is then used to send custom messages to known neighbors,
 * with their [Field] value used as message content.
 *
 * Results are [Aggregate.evolve]d across rounds,
 * namely, this function also stores the local value of the field as state,
 * and later invocations in successive rounds will not start from the provided [initial] value,
 * rather, from the previously computed local field value.
 */
inline fun <ID : Any, reified Shared> Aggregate<ID>.exchange(
    initial: Shared,
    noinline body: (Field<ID, Shared>) -> Field<ID, Shared>,
): Field<ID, Shared> = exchanging(initial) { field -> body(field).run { yielding { this } } }

/**
 * [share] implements efficient stateful data sharing.
 *
 * Namely, starting from an [initial] value of [Shared], it computes a [Field] of [Shared],
 * which is fed to the [body] function,
 * and the resulting [Shared] is then stored internally and sent to all neighbors.
 *
 * Results are [Aggregate.evolve]d across rounds,
 * namely, this function also stores the returned value as state,
 * and later invocations in successive rounds will not start from the provided [initial] value,
 * rather, from the previously computed result.
 */
inline fun <ID : Any, reified Shared> Aggregate<ID>.share(
    initial: Shared,
    noinline body: (Field<ID, Shared>) -> Shared,
): Shared = sharing(initial) { field -> body(field).run { yielding { this } } }

/**
 * [sharing] implements efficient stateful data sharing.
 *
 * Namely, starting from an [initial] value, it computes a [Shared] through the [body] function,
 * which is sent to all neighbors, and finally must return a [Returned] by calling [YieldingContext.yielding].
 *
 * Results are [Aggregate.evolve]d across rounds,
 * namely, this function also stores the local value of the field as state,
 * and later invocations in successive rounds will not start from the provided [initial] value,
 * rather, from the previously computed result.
 */
inline fun <ID : Any, reified Shared, Returned> Aggregate<ID>.sharing(
    initial: Shared,
    noinline body: YieldingContext<Shared, Returned>.(Field<ID, Shared>) -> YieldingResult<Shared, Returned>,
): Returned = exchanging(initial) { field: Field<ID, Shared> ->
    with(YieldingContext<Shared, Returned>()) {
        val result: YieldingResult<Shared, Returned> = body(field)
        field.map { result.toSend }.yielding {
            field.map { result.toReturn }
        }
    }
}.localValue
