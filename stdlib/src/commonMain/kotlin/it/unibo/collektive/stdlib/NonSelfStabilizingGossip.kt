/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.hood

/**
 * A non-self-stabilizing function for repeated propagation of a [value] and [aggregation]
 * of state estimates between neighboring devices.
 */
fun <ID : Any, Type> Aggregate<ID>.nonSelfStabilizingGossip(
    value: Type,
    aggregation: (Type, Type) -> Type,
): Type = share(value) {
    it.hood(value, aggregation)
}

/**
 * A "gossip" algorithm that computes whether any device has ever experienced a certain [condition] before.
 */
fun <ID : Any> Aggregate<ID>.nonSelfStabilizingEverHappenedGossip(
    condition: () -> Boolean,
    default: Boolean = false,
): Boolean = share(default) {
    condition() || it.fold(default) { a, b -> a || b }
}
