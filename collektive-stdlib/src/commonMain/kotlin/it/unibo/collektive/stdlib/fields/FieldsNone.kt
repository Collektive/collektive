/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import arrow.core.Predicate
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.FieldEntry
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType

/**
 * Returns `true` if no element in the field satisfies the given [predicate].
 *
 * The local entry is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [it.unibo.collektive.stdlib.util.ExcludingSelf]).
 *
 * @param reductionType controls whether the local entry is included in the check.
 * @param predicate the condition to test for each field entry.
 * @return `true` if no applicable entry satisfies the predicate, `false` otherwise.
 */
inline fun <ID : Any, T> Field<ID, T>.none(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = !any(reductionType, predicate)

/**
 * Returns `true` if no ID in the field satisfies the given [predicate].
 *
 * The local ID is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local ID is included in the check.
 * @param predicate the condition to test for each ID.
 * @return `true` if no applicable ID satisfies the predicate, `false` otherwise.
 */
inline fun <ID : Any> Field<ID, *>.noID(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Boolean = !anyID(reductionType, predicate)

/**
 * Returns `true` if no value in the field satisfies the given [predicate].
 *
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local value is included in the check.
 * @param predicate the condition to test for each value.
 * @return `true` if no applicable value satisfies the predicate, `false` otherwise.
 */
inline fun <T> Field<*, T>.noValue(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Boolean = !anyValue(reductionType, predicate)
