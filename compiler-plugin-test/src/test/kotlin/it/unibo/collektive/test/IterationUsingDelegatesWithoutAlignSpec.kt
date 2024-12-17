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
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.getTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.pascalCase
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

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
                addSource("src/test/resources/subjekt/IterationWithDelegatedAggregate.yaml")
            }.toTempFiles()

        forAll(testedAggregateFunctions) { functionCall ->
            val functionName = functionCall.substringBefore("(")
            forAll(formsOfIteration) { iteration, iterationDescription ->
                /**
                 * Gets the [KotlinTestingProgram] corresponding to a specific [case].
                 */
                fun getProgramFromCase(case: String): KotlinTestingProgram =
                    testSubjects.getTestingProgram(pascalCase(case, functionName, iteration))

                "inside $iterationDescription and using a function that takes an Aggregate argument" - {
                    val case = "IterationDelegate"
                    val code = getProgramFromCase(case)
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
                        val code = getProgramFromCase(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "with a specific alignedOn inside the called function" - {
                        val case = "IterationDelegateAlign"
                        val code = getProgramFromCase(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "but with aggregate calls within a nested function" - {
                        val case = "IterationDelegateWithNestedFun"
                        val code = getProgramFromCase(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "that recursively calls another function with an Aggregate argument" - {
                        val case = "IterationRecursiveDelegate"
                        val code = getProgramFromCase(case)
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
                        val code = getProgramFromCase(case)

                        "should compile producing a warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function 'delegate' that takes an Aggregate argument " +
                    "that recursively calls another function 'delegate2' with an Aggregate argument" +
                    "wrapped inside a specific alignedOn inside the 'delegate2' body" - {
                        val case = "IterationRecursiveDelegateAlign"
                        val code = getProgramFromCase(case)

                        "should compile producing a warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument, " +
                    "using it inside a called nested function" - {
                        val case = "IterationDelegatedNestedFun"
                        val code = getProgramFromCase(case)
                        val functionWithAggregateArgumentName = "delegate"

                        "should compile producing a warning".config(enabled = false) - {
                            code shouldCompileWith
                                warning(
                                    expectedWarning(functionWithAggregateArgumentName),
                                )
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument, " +
                    "using it inside a nested function, called by wrapping it with a specific alignedOn" - {
                        val case = "IterationAlignDelegatedNestedFun"
                        val code = getProgramFromCase(case)

                        "should compile producing a warning".config(enabled = false) - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument, " +
                    "using it with a specific alignedOn, inside a called nested function" - {
                        val case = "IterationDelegatedNestedFunAlign"
                        val code = getProgramFromCase(case)

                        "should compile producing a warning".config(enabled = false) - {
                            code shouldCompileWith noWarning
                        }
                    }
            }
        }
    }
})
