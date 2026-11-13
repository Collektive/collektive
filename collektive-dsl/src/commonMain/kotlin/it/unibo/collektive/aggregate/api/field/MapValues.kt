/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api.field

import it.unibo.collektive.aggregate.Field

/**
 * Map the field using the [transform] function.
 * By default, the [default] value of the field is mapped using [transform].
 */
inline fun <ID : Any, T, B> Field<ID, T>.mapValues(crossinline transform: (T) -> B): Field<ID, B> =
    map { (_, value) -> transform(value) }

/**
 * Map the field using the [transform] function.
 * The [Field.default] value of the field is obtained by invoking the [default] function.
 */
inline fun <ID : Any, T, B> Field<ID, T>.mapValues(
    crossinline default: () -> B,
    crossinline transform: (T) -> B,
): Field<ID, B> = map { (id, value) ->
    when (id) {
        null -> default()
        else -> transform(value)
    }
}

/**
 * Map the field using the [transform] function.
 * The [Field.default] value of the field is replaced by [default] function.
 */
inline fun <ID : Any, T, B> Field<ID, T>.mapValues(default: B, crossinline transform: (T) -> B): Field<ID, B> =
    mapValues({ default }, transform)
