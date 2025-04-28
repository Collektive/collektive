/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize.utils

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import org.gradle.internal.extensions.stdlib.capitalized
import kotlin.reflect.KCallable
import kotlin.reflect.KProperty

/**
 * Utilities for interoperate with kotlin poet.
 */
object KotlinPoetUtils {
    private fun KCallable<*>.isProperty() = this is KProperty<*>

    /**
     * Generate the body for function where the extension receiver is a `Field` and the function has no arguments.
     */
    internal fun FunSpec.Builder.addSingleStatementBodyFunction(callable: KCallable<*>) {
        val baseCall = "return·%N·{·it.${callable.name}"
        when {
            callable.isProperty() -> addStatement("$baseCall·}", FIELD_MAP)
            callable.typeParameters.isEmpty() -> addStatement("$baseCall()·}", FIELD_MAP)
            else -> addStatement("$baseCall<${callable.typeParameters.joinToString { it.name }}>()·}", FIELD_MAP)
        }
    }

    /**
     * Generate the body for function where the extension receiver is a `Field`.
     * The strategy is to call the `mapWithId` function on the receiver and use the `id` on arguments that are `Field`,
     * and the argument itself otherwise.
     */
    internal fun FunSpec.Builder.addBodyFunction(
        callable: KCallable<*>,
        parameters: List<ParameterSpec>,
        isReceiverField: Boolean,
    ) {
        when {
            callable.isProperty() -> {
                check(isReceiverField) {
                    "Bug in Collektivize expanding ${callable.name}"
                }
                addStatement(
                    "return ${parameters.first().name}.%N·{·receiver·->·receiver.%N }",
                    FIELD_MAP,
                    callable.name,
                )
            }
            else -> {
                /*
                 * Find the first field parameter, it will be the receiver of the alignedMap call.
                 * Then, find all the other field parameters, which will be passed as arguments to the alignedMap
                 * call.
                 * Also, their names mapped with a leading underscore will be used as lambda parameter names.
                 * Non-field parameters will be passed as is.
                 */
                fun ParameterSpec.fieldElementName() = "local${name.capitalized()}"
                val fieldParameters = parameters.filter { it.isField() }
                val receiverField = fieldParameters.first().name
                val receiver = when {
                    !isReceiverField -> parameters.first().name
                    fieldParameters.size == 1 -> "it"
                    else -> fieldParameters.first().fieldElementName()
                }
                val fieldsToPassAsArguments = fieldParameters.drop(1).joinToString(separator = ",·") { it.name }
                val fieldArguments = if (fieldsToPassAsArguments.isEmpty()) "" else "($fieldsToPassAsArguments)"
                val lambdaNames = when {
                    fieldParameters.size == 1 -> ""
                    else -> fieldParameters.joinToString(separator = ",·", postfix = "·->·") { it.fieldElementName() }
                }
                val arguments = parameters.drop(1).joinToString(separator = ",·") {
                    when {
                        !it.isField() -> it.name
                        fieldParameters.size == 1 -> "it"
                        else -> it.fieldElementName()
                    }
                }
                addStatement(
                    "return $receiverField.%N$fieldArguments·{·$lambdaNames$receiver.%N($arguments)·}",
                    if (fieldArguments.isEmpty()) FIELD_MAP else ALIGNED_MAP_VALUES,
                    callable.name,
                )
            }
        }
    }
}
