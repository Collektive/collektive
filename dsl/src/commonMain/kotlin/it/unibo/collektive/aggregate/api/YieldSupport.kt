/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api

/**
 * Context for yielding operations (exchanging, sharing).
 * Yielding operations operate on a [Shared] value (usually exchanged with neighbors),
 * but return a differently typed value [Returned] to the caller.
 */
class YieldingContext<Shared, Returned> {
    /**
     * Computes [toReturn] after the data exchange operation is complete.
     */
    fun Shared.yielding(toReturn: Shared.() -> Returned): YieldingResult<Shared, Returned> =
        YieldingResult(this, this.toReturn())
}

/**
 * Specifies the value [toSend] and the value [toReturn] of a yielding operator.
 */
data class YieldingResult<Shared, Return>(val toSend: Shared, val toReturn: Return)
