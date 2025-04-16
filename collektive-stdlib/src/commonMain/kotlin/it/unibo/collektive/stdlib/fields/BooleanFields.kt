/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.Field.Companion.fold
import kotlin.jvm.JvmOverloads

/**
 * Check if all the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The [base] value is used as a first element to start the fold operation at which the [predicate] is applied.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.all(base: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(base)) { acc, value -> acc && predicate(value) }

/**
 * Check if any of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The [base] value is used as a first element to start the fold operation at which the [predicate] is applied.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.any(base: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    fold(predicate(base)) { acc, value -> acc || predicate(value) }

/**
 * Check if none of the [Boolean] elements in the field are true by default.
 * If [predicate] is defined, it is applied to the elements.
 * The [base] value is used as a first element to start the fold operation at which the [predicate] is applied.
 */
@JvmOverloads
fun <ID : Any> Field<ID, Boolean>.none(base: Boolean, predicate: (Boolean) -> Boolean = { it }): Boolean =
    !all(base, predicate)
