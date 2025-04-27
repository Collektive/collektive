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
import it.unibo.collektive.aggregate.api.PurelyLocal
import it.unibo.collektive.aggregate.toFieldEntry
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import it.unibo.collektive.stdlib.util.init

/**
 * Returns `true` if all elements in the field satisfy the given [predicate].
 *
 * The local entry is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [it.unibo.collektive.stdlib.util.ExcludingSelf]).
 *
 * @param reductionType controls whether the local entry is included in the check.
 * @param predicate the condition to test for each field entry.
 * @return `true` if all applicable entries satisfy the predicate, `false` otherwise.
 */
@PurelyLocal
inline fun <ID : Any, T> Field<ID, T>.all(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<FieldEntry<ID, T>>,
): Boolean = reductionType.init(this, true, predicate) && excludeSelf().all { predicate(it.toFieldEntry()) }

/**
 * Returns `true` if all values in the field satisfy the given [predicate].
 *
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local value is included in the check.
 * @param predicate the condition to test for each value.
 * @return `true` if all applicable values satisfy the predicate, `false` otherwise.
 */
@PurelyLocal
inline fun <T> Field<*, T>.allValues(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<T>,
): Boolean = reductionType.init(this, true) { predicate(local.value) } && neighborsValues.all(predicate)

/**
 * Returns `true` if all IDs in the field satisfy the given [predicate].
 *
 * The local ID is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local ID is included in the check.
 * @param predicate the condition to test for each ID.
 * @return `true` if all applicable IDs satisfy the predicate, `false` otherwise.
 */
@PurelyLocal
inline fun <ID : Any> Field<ID, *>.allIDs(
    reductionType: ReductionType = ExcludingSelf,
    crossinline predicate: Predicate<ID>,
): Boolean = reductionType.init(this, true) { predicate(local.id) } && neighbors.all(predicate)
