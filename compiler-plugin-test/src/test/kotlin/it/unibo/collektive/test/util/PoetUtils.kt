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
import com.tschuchort.compiletesting.JvmCompilationResult
import it.unibo.collektive.test.util.CompileUtils.KotlinTestingProgram
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

object PoetUtils {

    val AGGREGATE = ClassName("it.unibo.collektive.aggregate.api", "Aggregate")

    @OptIn(ExperimentalCompilerApi::class)
    infix fun FileSpec.shouldCompileWith(compilationCheck: (JvmCompilationResult) -> Unit) {
        println(toString())
        KotlinTestingProgram(this.name, this.toString()).shouldCompileWith(compilationCheck)
    }

    @OptIn(ExperimentalCompilerApi::class)
    infix fun FileSpec.Builder.shouldCompileWith(compilationCheck: (JvmCompilationResult) -> Unit) {
        this.build() shouldCompileWith compilationCheck
    }

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

    infix operator fun FileSpec.plus(customization: FileSpec.Builder.() -> FileSpec.Builder): FileSpec =
        toBuilder().customization().build()

    infix operator fun FunSpec.plus(customization: FunSpec.Builder.() -> FunSpec.Builder): FunSpec =
        toBuilder().customization().build()

    infix fun FileSpec.withFunction(funSpec: FunSpec): FileSpec =
        this + { addFunction(funSpec) }

    fun FunSpec.Builder.block(
        header: String = "",
        content: FunSpec.Builder.() -> FunSpec.Builder,
    ): FunSpec.Builder =
        beginControlFlow(header)
            .content()
            .endControlFlow()

    fun FunSpec.Builder.blockS(
        header: String = "",
        content: () -> String,
    ): FunSpec.Builder =
        block(header) { addCode(content().trimIndent() + "\n") }

    fun FunSpec.Builder.loop(
        loopExpression: String = "i in 0..10",
        loopContent: FunSpec.Builder.() -> FunSpec.Builder,
    ): FunSpec.Builder =
        block("for($loopExpression)", loopContent)

    fun FunSpec.Builder.loopS(
        loopExpression: String = "i in 0..10",
        content: () -> String,
    ): FunSpec.Builder =
        loop(loopExpression) { addCode(content().trimIndent() + "\n") }

    fun FunSpec.Builder.alignedOn(pivot: String, content: FunSpec.Builder.() -> FunSpec.Builder): FunSpec.Builder =
        block("alignedOn($pivot)", content)

    fun FunSpec.Builder.alignedOnS(pivot: String, content: () -> String): FunSpec.Builder =
        alignedOn(pivot) { addCode(content().trimIndent() + "\n") }

    fun FileSpec.Builder.function(function: () -> FunSpec): FileSpec.Builder =
        addFunction(function())

    fun FunSpec.Builder.nestedFunction(
        header: String = "exampleName(): Unit",
        content: FunSpec.Builder.() -> FunSpec.Builder,
    ): FunSpec.Builder =
        block("fun $header", content)

    fun FunSpec.Builder.nestedFunctionS(
        header: String = "exampleName(): Unit",
        content: () -> String,
    ): FunSpec.Builder =
        nestedFunction(header) { addCode(content().trimIndent() + "\n") }
}
