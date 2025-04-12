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
 * never going below a [lowerBound].
 */
fun <ID : Comparable<ID>> Aggregate<ID>.timer(
    initial: Duration,
    decay: Duration,
    lowerBound: Duration = Duration.ZERO,
    decayRate: (timeLeft: Duration) -> Duration = { it - decay },
): Duration = evolve(initial) { timeLeft ->
    decayRate(timeLeft).coerceIn(lowerBound, initial)
}
