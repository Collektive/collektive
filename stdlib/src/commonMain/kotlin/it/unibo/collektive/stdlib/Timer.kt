/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib

import it.unibo.collektive.aggregate.api.Aggregate
import kotlin.time.Duration

/**
 * A timer that decays over time.
 * The timer starts at [initial] and decays at a rate determined by the [decayRate] function,
 * never going below a [lower bound].
 */
fun <ID : Comparable<ID>> Aggregate<ID>.timer(
    initial: Duration,
    lowerBound: Duration,
    decayRate: (Duration) -> Duration,
): Duration =
    repeat(initial) { time ->
        min(initial, max(lowerBound, decayRate(time)))
    }

private fun max(first: Duration, second: Duration): Duration =
    min(second, first)

private fun min(first: Duration, second: Duration): Duration =
    if (first <= second) first else second
