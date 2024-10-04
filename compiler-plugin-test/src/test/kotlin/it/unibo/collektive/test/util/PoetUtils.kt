/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object PoetUtils {

    val AGGREGATE = ClassName("it.unibo.collektive.aggregate.api", "Aggregate")

    fun simpleTestingFileWithAggregate(fileName: String = "SimpleTestingFileWithAggregate.kt"): FileSpec =
        FileSpec.builder("", fileName)
            .addImport(AGGREGATE, "")
            .addFunction(
                FunSpec.builder("exampleAggregate")
                    .receiver(AGGREGATE.parameterizedBy(INT))
                    .returns(Int::class)
                    .addStatement("return 0")
                    .build(),
            ).build()

    fun simpleAggregateFunction(aggregateType: ClassName, name: String = "containerFun"): FunSpec =
        FunSpec.builder(name).receiver(AGGREGATE.parameterizedBy(aggregateType)).build()

    infix operator fun FileSpec.rem(customization: FileSpec.Builder.() -> FileSpec.Builder): FileSpec =
        toBuilder().customization().build()

    infix operator fun FunSpec.rem(customization: FunSpec.Builder.() -> FunSpec.Builder): FunSpec =
        toBuilder().customization().build()

    fun FunSpec.Builder.wrapper(
        expression: String,
        content: FunSpec.Builder.() -> FunSpec.Builder,
    ): FunSpec.Builder =
        beginControlFlow(expression)
            .content()
            .endControlFlow()

    fun FunSpec.Builder.loop(
        loopExpression: String = "i in 0..10",
        loopContent: FunSpec.Builder.() -> FunSpec.Builder,
    ): FunSpec.Builder =
        wrapper("for($loopExpression)", loopContent)

    fun FunSpec.Builder.alignedOn(pivot: String, loopContent: FunSpec.Builder.() -> FunSpec.Builder): FunSpec.Builder =
        wrapper("alignedOn($pivot)", loopContent)
}
