/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.extensions.kotlin

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import it.unibo.collektive.collektivize.autoConversions
import it.unibo.collektive.collektivize.extensions.kotlinpoet.projected
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter

private val specializedArrayTypes = setOf(
    IntArray::class,
    DoubleArray::class,
    LongArray::class,
    FloatArray::class,
    ShortArray::class,
    ByteArray::class,
    CharArray::class,
    BooleanArray::class,
)

internal fun KType?.toTypeNameWithRecurringGenericSupport(
    recurringTypeArguments: Set<KTypeParameter> = emptySet(),
): TypeName {
    if (this == null) {
        return STAR
    }
    val classifier = classifier

    fun ClassName.parameterized(): TypeName = run {
        when {
            arguments.isEmpty() -> this
            specializedArrayTypes.contains(classifier) -> this // No type arguments expected for class '*Array'.
            else ->
                parameterizedBy(
                    arguments.map {
                        it.toTypeNameWithRecurringGenericSupport(recurringTypeArguments).projected()
                    },
                )
        }
    }
    return when (classifier) {
        is KClass<*> -> {
            when (val qualifiedName = classifier.qualifiedName) {
                null -> error("Cannot generate types for anonymous class $classifier")
                in autoConversions -> {
                    autoConversions.getValue(qualifiedName).parameterized()
                }
                else -> {
                    ClassName.bestGuess(qualifiedName).parameterized().copy(nullable = isMarkedNullable)
                }
            }
        }
        is KTypeParameter -> classifier.toTypeVariableName(recurringTypeArguments).copy(nullable = isMarkedNullable)
        else -> asTypeName()
    }
}
