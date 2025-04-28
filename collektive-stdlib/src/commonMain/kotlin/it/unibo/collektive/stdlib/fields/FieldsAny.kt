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
import it.unibo.collektive.aggregate.toFieldEntry
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import it.unibo.collektive.stdlib.util.init

/**
 * Returns `true` if any element in the field satisfies the given [predicate].
 *
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [it.unibo.collektive.stdlib.util.ExcludingSelf]).
 *
 * @param reductionType controls whether the local entry is included in the check.
 * @param predicate the condition to test for each field entry.
 * @return `true` if at least one matching entry exists, `false` otherwise.
 */
inline fun <ID : Any, T> Field<ID, T>.any(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = reductionType.init(this, false, predicate) || excludeSelf().any { predicate(it.toFieldEntry()) }

/**
 * Returns `true` if any value in the field satisfies the given [predicate].
 *
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local value is included in the check.
 * @param predicate the condition to test for each value.
 * @return `true` if at least one matching value exists, `false` otherwise.
 */
inline fun <T> Field<*, T>.anyValue(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Boolean = reductionType.init(this, false) { predicate(it.value) } || neighborsValues.any(predicate)

/**
 * Returns `true` if any ID in the field satisfies the given [predicate].
 *
 * The local ID is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local ID is included in the check.
 * @param predicate the condition to test for each ID.
 * @return `true` if at least one matching ID exists, `false` otherwise.
 */
inline fun <ID : Any> Field<ID, *>.anyID(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Boolean = reductionType.init(this, false) { predicate(it.id) } || neighbors.any(predicate)
