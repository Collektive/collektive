/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.LoopWithoutAlignTest.Companion.EXPECTED_WARNING_MESSAGE
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.ProgramTemplates
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class IterationWithoutAlignTest : FreeSpec({
    "When using an iterative function like 'forEach' and similar" - {
        val testingProgramTemplate = CompileUtils.testingProgramFromTemplate(
            ProgramTemplates.SINGLE_AGGREGATE_IN_ITERATIVE_FUNCTION,
        )
        listOf(
            "exampleAggregate" to "exampleAggregate()",
            "neighboring" to "neighboring(0)",
            "exchange" to "exchange(0) { it }",
        ).flatMap { aggregateFunctionPair ->
            listOf(
                "forEach" to "",
                "filter" to "false",
                "map" to "",
                "flatMap" to "listOf(null)",
            ).map {
                aggregateFunctionPair to it
            }
        }
            .forEach { (functionPair, iterativeMethodAndCode) ->
                val (functionName, functionCall) = functionPair
                val (iterativeMethod, recoveryCode) = iterativeMethodAndCode
                "using $functionName without a specific alignedOn" - {
                    "should produce a warning" - {
                        val testingProgram = testingProgramTemplate
                            .put("aggregate", functionCall)
                            .put("afterAggregate", recoveryCode)
                            .put("iterativeFunction", iterativeMethod)
                        testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format(functionName))
                    }
                }
                "using $functionName wrapped in a specific alignedOn" - {
                    val testingProgram = testingProgramTemplate
                        .put("beforeAggregate", "alignedOn(0) {")
                        .put("afterAggregate", "}\n$recoveryCode")
                        .put("iterativeFunction", iterativeMethod)
                    "should compile without any warning" - {
                        testingProgram shouldCompileWith noWarning
                    }
                }
                "using $functionName wrapped in a specific alignedOn outside the loop" - {
                    val testingProgram = testingProgramTemplate
                        .put("beforeLoop", "alignedOn(0) {")
                        .put("afterLoop", "}")
                        .put("afterAggregate", recoveryCode)
                        .put("iterativeFunction", iterativeMethod)
                    "should produce a warning" - {
                        val testingProgramWithCustomFunction = testingProgram
                            .put("aggregate", functionCall)
                        testingProgramWithCustomFunction shouldCompileWith warning(
                            EXPECTED_WARNING_MESSAGE.format(functionName),
                        )
                    }
                }
                "using $functionName wrapped inside another function declaration" - {
                    val testingProgram = testingProgramTemplate
                        .put("beforeAggregate", "fun Aggregate<Int>.test() {")
                        .put("afterAggregate", "}\n$recoveryCode")
                        .put("iterativeFunction", iterativeMethod)
                    "should compile without any warning" - {
                        testingProgram shouldCompileWith noWarning
                    }
                }
            }
    }
})
