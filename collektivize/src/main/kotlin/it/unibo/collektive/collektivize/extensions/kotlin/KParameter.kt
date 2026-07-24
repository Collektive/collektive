/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.collektivize.FIELD_INTERFACE
import it.unibo.collektive.collektivize.ID_BOUNDED_TYPE
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

internal fun KParameter.isFunctionType(): Boolean = (type.classifier as? KClass<*>)
    ?.qualifiedName
    ?.startsWith("kotlin.Function") == true

/**
 * Given a [KParameter], returns a [ParameterSpec] where the type is a [Field] of the same type as the parameter.
 * Returns `null` if the "fielded" parameter would be shadowed.
 */
internal fun KParameter.toFieldParameterSpec(): ParameterSpec? {
    val willBeShadowed = type == typeOf<Field<*, *>>() || type.isSupertypeOf(typeOf<Field<*, *>>())
    return when (willBeShadowed) {
        true -> null // this parameter will be shadowed, do not generate it (will be removed later)
        false ->
            ParameterSpec(
                name ?: "this",
                FIELD_INTERFACE.parameterizedBy(
                    ID_BOUNDED_TYPE,
                    type.toTypeNameWithRecurringGenericSupport(),
                ),
            )
    }
}

internal fun KParameter.toParameterSpec(origin: KCallable<*>): ParameterSpec {
    val typeName = type.toTypeNameWithRecurringGenericSupport()
    return ParameterSpec(
        name = name ?: "this",
        type = typeName.copy(annotations = emptyList()),
        modifiers =
        when {
            origin is KFunction<*> && origin.isInline && isFunctionType() ->
                listOf(KModifier.CROSSINLINE)
            else -> emptyList()
        },
    )
}
