/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import arrow.core.identity
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.stdlib.util.ExcludingSelf
import it.unibo.collektive.stdlib.util.ReductionType
import kotlin.jvm.JvmOverloads

/**
 * Returns `true` if all Boolean values in the field are `true`.
 *
 * This overload assumes the identity predicate (i.e., it checks that all values are `true`).
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local value is included in the check.
 * @return `true` if all applicable values are `true`, `false` otherwise.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.allValues(reductionType: ReductionType = ExcludingSelf): Boolean =
    allValues(reductionType, ::identity)

/**
 * Returns `true` if at least one Boolean value in the field is `true`.
 *
 * This overload assumes the identity predicate (i.e., it checks whether any value is `true`).
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local value is included in the check.
 * @return `true` if any applicable value is `true`, `false` otherwise.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.anyValue(reductionType: ReductionType = ExcludingSelf): Boolean =
    anyValue(reductionType, ::identity)

/**
 * Returns `true` if no Boolean values in the field are `true`.
 *
 * This overload assumes the identity predicate (i.e., it checks that all values are `false`).
 * The local value is included in the check if [reductionType] is [it.unibo.collektive.stdlib.util.IncludingSelf]
 * (the default is [ExcludingSelf]).
 *
 * @param reductionType controls whether the local value is included in the check.
 * @return `true` if all applicable values are `false`, `false` otherwise.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.noValue(reductionType: ReductionType = ExcludingSelf): Boolean =
    noValue(reductionType, ::identity)
