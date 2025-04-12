/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.field.Field

/**
 * Coerce this value to be in the range between [min] and [max].
 */
fun <T : Comparable<T>> T.coerceIn(min: T, max: T): T = minOf(max, maxOf(this, min))

/**
 * Coerce this value in a [range].
 */
fun <T : Comparable<T>> T.coerceIn(range: ClosedRange<T>): T = minOf(range.endInclusive, maxOf(this, range.start))

/**
 * Coerce all field values to be in the range between [min] and [max].
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.coerceIn(min: T, max: T): Field<ID, T> = map { it.coerceIn(min, max) }

/**
 * Coerces all field values in a [range].
 */
fun <ID : Any, T : Comparable<T>> Field<ID, T>.coerceIn(range: ClosedRange<T>): Field<ID, T> =
    map { it.coerceIn(range) }
