/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api.field

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.Field.Fields.checkAligned

/**
 * Combines this field with another (aligned) one considering the [ID] when combining the values.
 */
inline fun <ID : Any, T, B, R> Field<ID, T>.alignedMap(
    other: Field<ID, B>,
    crossinline transform: (ID?, T, B) -> R,
): Field<ID, R> {
    checkAligned(this, other)
    return map { transform(it.id, it.value, other[it.id]) }
}

/**
 * Combines this field with other two (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, R> Field<ID, T>.alignedMap(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    crossinline transform: (ID?, T, B, C) -> R,
): Field<ID, R> {
    checkAligned(this, f1, f2)
    return map { (id, value) -> transform(id, value, f1[id], f2[id]) }
}

/**
 * Combines this field with other three (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, R> Field<ID, T>.alignedMap(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    crossinline transform: (ID?, T, B, C, D) -> R,
): Field<ID, R> {
    checkAligned(this, f1, f2, f3)
    return map { (id, value) -> transform(id, value, f1[id], f2[id], f3[id]) }
}

/**
 * Combines this field with other four (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, E, R> Field<ID, T>.alignedMap(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    f4: Field<ID, E>,
    crossinline transform: (ID?, T, B, C, D, E) -> R,
): Field<ID, R> {
    checkAligned(this, f1, f2, f3, f4)
    return map { (id, value) -> transform(id, value, f1[id], f2[id], f3[id], f4[id]) }
}

/**
 * Combines this field with other five (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, E, F, R> Field<ID, T>.alignedMap(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    f4: Field<ID, E>,
    f5: Field<ID, F>,
    crossinline transform: (ID?, T, B, C, D, E, F) -> R,
): Field<ID, R> {
    checkAligned(this, f1, f2, f3, f4, f5)
    return map { (id, value) -> transform(id, value, f1[id], f2[id], f3[id], f4[id], f5[id]) }
}

/**
 * Combines this field with other six (aligned) fields based on the [ID]s.
 */
inline fun <ID : Any, T, B, C, D, E, F, G, R> Field<ID, T>.alignedMap(
    f1: Field<ID, B>,
    f2: Field<ID, C>,
    f3: Field<ID, D>,
    f4: Field<ID, E>,
    f5: Field<ID, F>,
    f6: Field<ID, G>,
    crossinline transform: (ID?, T, B, C, D, E, F, G) -> R,
): Field<ID, R> {
    checkAligned(this, f1, f2, f3, f4, f5, f6)
    return map { (id, value) -> transform(id, value, f1[id], f2[id], f3[id], f4[id], f5[id], f6[id]) }
}
