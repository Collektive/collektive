/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.aggregate.toFieldEntry
import it.unibo.collektive.stdlib.util.Reducer
import it.unibo.collektive.stdlib.util.SummaryWithSelf
import it.unibo.collektive.stdlib.util.SummaryWithoutSelf

/**
 * Reduces the field entries to a single value using the given [reducer] function,
 * excluding the local element.
 *
 * This function performs a reduction over the field entries (excluding the local one), combining them
 * pairwise using [reducer]. If the field contains no neighbor entries, the result is `null`.
 *
 * @param reducer a binary operation that reduces two entries into one.
 * @return the result of the reduction, or `null` if the field has no neighbors.
 */
inline fun <ID : Any, T> SummaryWithoutSelf<ID, T>.reduce(crossinline reducer: Reducer<FieldEntry<ID, T>>): FieldEntry<ID, T>? =
    field.excludeSelf().entries.asSequence().map { it.toFieldEntry() }.reduceOrNull(reducer)

inline fun <ID : Any, T> SummaryWithSelf<ID, T>.reduce(crossinline reducer: Reducer<FieldEntry<ID, T>>): FieldEntry<ID, T> =
    field.toMap().entries.asSequence().map { it.toFieldEntry() }.reduce(reducer)

/**
 * Reduces the field IDs to a single value using the given [reducer] function,
 * excluding the local element.
 *
 * This is a convenience reduction applied only to the field keys (IDs).
 * If there are no neighbors, the result is `null`.
 *
 * @param reducer a binary operation that reduces two IDs into one.
 * @return the result of the reduction, or `null` if the field has no neighbors.
 */
inline fun <ID : Any> SummaryWithoutSelf<ID, *>.reduceIDs(crossinline reducer: Reducer<ID>): ID? =
    field.excludeSelf().keys.reduceOrNull(reducer)

inline fun <ID : Any> SummaryWithSelf<ID, *>.reduceIDs(crossinline reducer: Reducer<ID>): ID =
    field.toMap().keys.reduce(reducer)

/**
 * Reduces the field values to a single value using the given [reducer] function,
 * excluding the local element.
 *
 * This is a convenience reduction applied only to the field values.
 * If there are no neighbors, the result is `null`.
 *
 * @param reducer a binary operation that reduces two values into one.
 * @return the result of the reduction, or `null` if the field has no neighbors.
 */
inline fun <ID : Any, T> Field<ID, T>.reduceValues(crossinline reducer: Reducer<T>): T? =
    excludeSelf().values.reduceOrNull(reducer)
