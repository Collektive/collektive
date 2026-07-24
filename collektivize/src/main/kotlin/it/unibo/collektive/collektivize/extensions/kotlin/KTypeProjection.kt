/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection

internal fun KTypeProjection.toTypeNameWithRecurringGenericSupport(
    recurringTypeArguments: Set<KTypeParameter> = emptySet(),
): TypeName = when (val result = type.toTypeNameWithRecurringGenericSupport(recurringTypeArguments)) {
    is TypeVariableName ->
        TypeVariableName(name = result.name, variance = variance.toKModifier()).copy(
            nullable = result.isNullable,
            annotations = result.annotations,
            reified = result.isReified,
        )
    else -> result
}
