/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import arrow.core.identity
import it.unibo.collektive.aggregate.Collapse

/**
 * Returns `true` if every value in the collapsed field is `true`.
 *
 * If the collapsed collection is empty, this returns `true` (vacuous truth). Evaluation
 * stops early when a `false` is encountered.
 */
val Collapse<Boolean>.all: Boolean get() = sequence.all(::identity)

/**
 * Returns `true` if at least one value in the collapsed field is `true`.
 *
 * If the collapsed collection is empty, this returns `false`. Evaluation stops early when a
 * `true` is found.
 */
val Collapse<Boolean>.any: Boolean get() = sequence.any()

/**
 * Returns `true` if no value in the collapsed field is `true`.
 *
 * Equivalent to `!any`. If the collapsed collection is empty, this returns `true`.
 */
val Collapse<Boolean>.none: Boolean get() = sequence.none()
