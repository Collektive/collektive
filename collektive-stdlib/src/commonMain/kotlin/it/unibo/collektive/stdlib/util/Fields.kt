/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import arrow.core.Predicate
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry

/**
 * Returns a new field where each value matching the given [predicate] is replaced with [replacement].
 *
 * Entries that do not satisfy the [predicate] retain their original value.
 * The local value is included in the check.
 *
 * @param replacement the value to assign to matching entries.
 * @param predicate the condition used to determine which values to replace.
 * @return a new field with values replaced where the predicate is satisfied.
 */
inline fun <ID : Any, T> Field<ID, T>.replaceMatchingValues(
    replacement: T,
    crossinline predicate: Predicate<T>,
): Field<ID, T> = replaceMatching(replacement, { (_, value) -> predicate(value) })

/**
 * Returns a new field where each entry matching the given [predicate] is replaced with [replacement].
 *
 * Entries that do not satisfy the [predicate] retain their original value.
 * The local entry is included in the check.
 *
 * @param replacement the value to assign to matching entries.
 * @param predicate the condition used to determine which entries to replace.
 * @return a new field with values replaced where the predicate is satisfied.
 */
inline fun <ID : Any, T> Field<ID, T>.replaceMatching(
    replacement: T,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Field<ID, T> = map { (id, value) -> if (predicate(FieldEntry(id, value))) replacement else value }
