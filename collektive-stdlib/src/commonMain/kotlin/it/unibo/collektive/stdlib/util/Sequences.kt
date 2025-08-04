/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.util

import it.unibo.collektive.aggregate.FieldEntry

/**
 * Returns a sequence of the IDs from each [FieldEntry] in the original sequence.
 *
 * The value portion of the entries is ignored. This is useful when only the identifiers
 * of the field entries are needed, and the underlying sequence is to be processed lazily.
 *
 * @return a lazy [Sequence] of the entry IDs.
 */
fun <ID : Any> Sequence<FieldEntry<ID, *>>.ids(): Sequence<ID> = map { it.id }

/**
 * Returns a sequence of the values from each [FieldEntry] in the original sequence.
 *
 * The ID portion of the entries is ignored. This is useful when only the payload values
 * of the field entries are needed, and the underlying sequence is to be processed lazily.
 *
 * @return a lazy [Sequence] of the entry values.
 */
fun <T> Sequence<FieldEntry<*, T>>.values(): Sequence<T> = map { it.value }
