/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import it.unibo.collektive.aggregate.Collapse

/**
 * Returns the maximum of all values in this collapse, or [Int.MIN_VALUE] if the collapse is an empty
 * [it.unibo.collektive.aggregate.CollapsePeers].
 */
val Collapse<Int>.max: Int get() = max(Int.MIN_VALUE)

/**
 * Returns the minimum of all values in this collapse, or [Int.MAX_VALUE] if the collapse is an empty
 * [it.unibo.collektive.aggregate.CollapsePeers].
 */
val Collapse<Int>.min: Int get() = min(Int.MAX_VALUE)

/**
 * Returns the product of all values in this collapse.
 */
val Collapse<Int>.product: Int get() = sequence.fold(1, Int::times)

/**
 * Returns the sum of all values in this collapse.
 */
val Collapse<Int>.sum: Int get() = sequence.fold(0, Int::plus)
