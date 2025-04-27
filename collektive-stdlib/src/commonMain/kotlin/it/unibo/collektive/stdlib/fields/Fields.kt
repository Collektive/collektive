/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.stdlib.fields

import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.PurelyLocal

/**
 * Checks whether the given [value] is present in the field, including the local value.
 *.
 * This function returns `true` if [value] is equal to the local value or is contained
 * in the values of the neighboring entries.
 *
 * @param value the value to look for.
 * @return `true` if [value] is present in the field, `false` otherwise.
 */
@PurelyLocal
fun <T> Field<*, T>.containsValue(value: T): Boolean = local.value == value || neighborsValues.contains(value)
