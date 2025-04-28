/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import arrow.core.fold
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.stdlib.util.Accumulator

/**
 * Accumulates a result starting from [initial], by applying [accumulator] to each field entry
 * (excluding the local one).
 *
 * This function behaves like a fold over the entries of the field, skipping the local element.
 *
 * @param initial the initial value for the accumulation.
 * @param accumulator the function that combines the current accumulator value with each field entry.
 * @return the final accumulated result.
 */
inline fun <ID : Any, T, R> Field<ID, T>.fold(
    initial: R,
    crossinline accumulator: Accumulator<R, FieldEntry<ID, T>>,
): R = excludeSelf().fold(initial) { current, (id, value) -> accumulator(current, FieldEntry(id, value)) }

/**
 * Accumulates a result starting from [initial], by applying [accumulator] to each ID in the field
 * (excluding the local one).
 *
 * This is a convenience fold that operates only on field entry IDs.
 *
 * @param initial the initial value for the accumulation.
 * @param accumulator the function that combines the current accumulator value with each entry's ID.
 * @return the final accumulated result.
 */
inline fun <ID : Any, R> Field<ID, *>.foldIDs(initial: R, crossinline accumulator: Accumulator<R, ID>): R =
    fold(initial) { current, (id, _) -> accumulator(current, id) }

/**
 * Accumulates a result starting from [initial], by applying [accumulator] to each value in the field
 * (excluding the local one).
 *
 * This is a convenience fold that operates only on field entry values.
 *
 * @param initial the initial value for the accumulation.
 * @param accumulator the function that combines the current accumulator value with each entry's value.
 * @return the final accumulated result.
 */
inline fun <T, R> Field<*, T>.foldValues(initial: R, crossinline accumulator: Accumulator<R, T>): R =
    fold(initial) { current, (_, value) -> accumulator(current, value) }
