/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api.field

import it.unibo.collektive.aggregate.Field

/**
 * Combines this field with another (aligned) one.
 */
inline fun <ID : Any, T, B, R> Field<ID, T>.alignedMapValues(
    other: Field<ID, B>,
    crossinline transform: (T, B) -> R,
): Field<ID, R> = alignedMap(other) { _, value, otherValue -> transform(value, otherValue) }

/**
 * Combines this field with other two (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, R> Field<ID, T>.alignedMapValues(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    crossinline transform: (T, B, C) -> R,
): Field<ID, R> = alignedMap(f1, f2) { _, value, f1Value, f2Value -> transform(value, f1Value, f2Value) }

/**
 * Combines this field with other three (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, R> Field<ID, T>.alignedMapValues(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    crossinline transform: (T, B, C, D) -> R,
): Field<ID, R> =
    alignedMap(f1, f2, f3) { _, value, f1Value, f2Value, f3Value -> transform(value, f1Value, f2Value, f3Value) }

/**
 * Combines this field with other four (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, E, R> Field<ID, T>.alignedMapValues(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    f4: Field<ID, E>,
    crossinline transform: (T, B, C, D, E) -> R,
): Field<ID, R> = alignedMap(f1, f2, f3, f4) { _, value, f1Value, f2Value, f3Value, f4Value ->
    transform(value, f1Value, f2Value, f3Value, f4Value)
}

/**
 * Combines this field with other five (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, E, F, R> Field<ID, T>.alignedMapValues(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    f4: Field<ID, E>,
    f5: Field<ID, F>,
    crossinline transform: (T, B, C, D, E, F) -> R,
): Field<ID, R> = alignedMap(f1, f2, f3, f4, f5) { _, value, f1Value, f2Value, f3Value, f4Value, f5Value ->
    transform(value, f1Value, f2Value, f3Value, f4Value, f5Value)
}

/**
 * Combines this field with other six (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, E, F, G, R> Field<ID, T>.alignedMapValues(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    f4: Field<ID, E>,
    f5: Field<ID, F>,
    f6: Field<ID, G>,
    crossinline transform: (T, B, C, D, E, F, G) -> R,
): Field<ID, R> = alignedMap(f1, f2, f3, f4, f5, f6) { _, value, f1Value, f2Value, f3Value, f4Value, f5Value, f6Value ->
    transform(value, f1Value, f2Value, f3Value, f4Value, f5Value, f6Value)
}
