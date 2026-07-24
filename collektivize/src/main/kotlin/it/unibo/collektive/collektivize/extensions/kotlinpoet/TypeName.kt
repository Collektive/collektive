/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlinpoet

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName

/**
 * Given a type, returns a list of all generic types defined in it recursively.
 * Examples:
 * - For `Field<ID, Array<T>>`, it returns `ID` and `T`.
 * - For `Map<ID, Pair<E, F>>`, it returns `ID`, `E`, and F`.
 * - For `Field<ID, Field<ID, T>>`, it returns `ID` and `T`.
 * - For `Field<ID, Map<T, Pair<A, B>>` it returns `ID`, `T`, `A`, and `B`.
 *
 * Since this function is used to generate the type variables for the generated functions,
 * the variance of the type variables is not considered since kotlin does not allow variance
 * in type variables for functions.
 */
internal fun TypeName.getAllTypeVariables(): List<TypeVariableName> = when (this) {
    is TypeVariableName -> listOf(TypeVariableName(this.name, this.bounds, null))
    is ParameterizedTypeName -> typeArguments.flatMap { it.getAllTypeVariables() }
    else -> emptyList()
}

internal fun TypeName.projected(): TypeName = when (this) {
    is TypeVariableName -> {
        when (variance) {
            KModifier.IN -> WildcardTypeName.consumerOf(this)
            KModifier.OUT -> WildcardTypeName.producerOf(this)
            else -> this
        }
    }
    else -> this
}
