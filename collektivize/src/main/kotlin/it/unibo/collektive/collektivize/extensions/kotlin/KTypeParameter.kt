/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import com.squareup.kotlinpoet.TypeVariableName
import kotlin.reflect.KTypeParameter

internal fun KTypeParameter.toTypeVariableName(
    recurryingTypeArguments: Set<KTypeParameter> = emptySet(),
): TypeVariableName = when {
    this in recurryingTypeArguments ->
        TypeVariableName(
            name = name,
            variance = variance.toKModifier(),
        ).copy(reified = isReified)
    else -> {
        val unbound = TypeVariableName(name)
        val upperBounds =
            upperBounds
                .filterNot { it.isMarkedNullable && it.classifier == Any::class }
                .map {
                    it.toTypeNameWithRecurringGenericSupport(recurryingTypeArguments + this)
                }
        unbound.copy(
            bounds = upperBounds,
            reified = isReified,
        )
    }
}
