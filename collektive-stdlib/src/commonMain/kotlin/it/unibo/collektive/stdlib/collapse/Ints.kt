/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.collapse

import it.unibo.collektive.aggregate.Collapse
import it.unibo.collektive.aggregate.CollapsePeers

/**
 * Returns the maximum of all values in this collapse, or [Int.MIN_VALUE] if the collapse is an empty
 * [it.unibo.collektive.aggregate.CollapsePeers].
 */
inline val CollapsePeers<Int>.max: Int get() = fold(Int.MIN_VALUE, ::maxOf)

/**
 * Returns the minimum of all values in this collapse, or [Int.MAX_VALUE] if the collapse is an empty
 * [it.unibo.collektive.aggregate.CollapsePeers].
 */
inline val CollapsePeers<Int>.min: Int get() = fold(Int.MAX_VALUE, ::maxOf)

/**
 * Returns the product of all values in this collapse.
 */
inline val Collapse<Int>.product: Int get() = sequence.fold(1, Int::times)

/**
 * Returns the sum of all values in this collapse.
 */
inline val Collapse<Int>.sum: Int get() = sequence.fold(0, Int::plus)
