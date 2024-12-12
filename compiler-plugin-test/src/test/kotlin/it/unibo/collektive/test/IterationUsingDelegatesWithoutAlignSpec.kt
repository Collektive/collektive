/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toTempFiles
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import it.unibo.collektive.test.util.CompileUtils.KotlinTestingProgram
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.pascalCase
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.FileNotFoundException

@OptIn(ExperimentalCompilerApi::class)
class IterationUsingDelegatesWithoutAlignSpec : FreeSpec({
    fun expectedWarning(functionName: String): String =
        """
        Function '$functionName', that accepts and uses an aggregate argument, has been called inside a loop construct 
        without explicit alignment.
        The same path may generate interactions more than once, leading to ambiguous alignment.
        
        Consider to wrap the function into the 'alignedOn' method with a unique element, either at the call site
        or inside the '$functionName' function declaration, wrapping the involved aggregate calls.
        """.trimIndent()

    "When iterating a function that takes an aggregate argument" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/IterationWithAggregate.yaml")
            }.toTempFiles()

        forAll(testedAggregateFunctions) { functionCall ->
            val functionName = functionCall.substringBefore("(")
            forAll(formsOfIteration) { iteration, iterationDescription ->
                /**
                 * Gets the text from a map of files, given its [caseName], and converts it to a
                 * [KotlinTestingProgram].
                 */
                fun getTestingProgram(caseName: String): KotlinTestingProgram =
                    testSubjects[pascalCase(caseName, functionName, iteration)]
                        ?.readText()
                        ?.asTestingProgram("$functionName-${caseName}_$iteration.kt")
                        ?: throw FileNotFoundException(
                            "Program not found: ${pascalCase(caseName, functionName, iteration)}",
                        )

                "inside $iterationDescription and using a function that takes an Aggregate argument" - {
                    val case = "IterationDelegate"
                    val code = getTestingProgram(case)
                    val functionWithAggregateArgumentName = "delegate"

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(functionWithAggregateArgumentName),
                            )
                    }
                }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "wrapped in a specific alignedOn" - {
                        val case = "IterationAlignDelegate"
                        val code = getTestingProgram(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "with a specific alignedOn inside the called function" - {
                        val case = "IterationDelegateAlign"
                        val code = getTestingProgram(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "but with aggregate calls within a nested function" - {
                        val case = "IterationDelegateWithNestedFun"
                        val code = getTestingProgram(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "that recursively calls another function with an Aggregate argument" - {
                        val case = "IterationRecursiveDelegate"
                        val code = getTestingProgram(case)
                        val functionWithAggregateArgumentName = "delegate"

                        "should compile producing a warning" - {
                            code shouldCompileWith
                                warning(
                                    expectedWarning(functionWithAggregateArgumentName),
                                )
                        }
                    }

                "inside $iterationDescription and using a function 'delegate' that takes an Aggregate argument " +
                    "that recursively calls another function 'delegate2' with an Aggregate argument" +
                    "wrapped inside a specific alignedOn inside the 'delegate' body" - {
                        val case = "IterationAlignRecursiveDelegate"
                        val code = getTestingProgram(case)

                        "should compile producing a warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function 'delegate' that takes an Aggregate argument " +
                    "that recursively calls another function 'delegate2' with an Aggregate argument" +
                    "wrapped inside a specific alignedOn inside the 'delegate2' body" - {
                        val case = "IterationRecursiveDelegateAlign"
                        val code = getTestingProgram(case)

                        "should compile producing a warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

// CURRENTLY NOT CAPTURED
//                "inside $iterationDescription and using a function that takes an Aggregate argument, " +
//                        "using it inside a called nested function" - {
//                    val case = "IterationDelegatedNestedFun"
//                    val code = getTestingProgram(case)
//                    val functionWithAggregateArgumentName = "delegate"
//
//                    "should compile producing a warning" - {
//                        code shouldCompileWith warning(
//                            expectedWarning(functionWithAggregateArgumentName),
//                        )
//                    }
//                }
//
//                "inside $iterationDescription and using a function that takes an Aggregate argument, " +
//                        "using it inside a nested function, called by wrapping it with a specific alignedOn" - {
//                    val case = "IterationAlignDelegatedNestedFun"
//                    val code = getTestingProgram(case)
//
//                    "should compile producing a warning" - {
//                        code shouldCompileWith noWarning
//                    }
//                }
//
//                "inside $iterationDescription and using a function that takes an Aggregate argument, " +
//                        "using it with a specific alignedOn, inside a called nested function" - {
//                    val case = "IterationDelegatedNestedFunAlign"
//                    val code = getTestingProgram(case)
//
//                    "should compile producing a warning" - {
//                        code shouldCompileWith noWarning
//                    }
//                }
            }
        }
    }
})
