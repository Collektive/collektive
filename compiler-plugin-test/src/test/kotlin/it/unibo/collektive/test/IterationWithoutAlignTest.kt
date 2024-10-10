/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import com.squareup.kotlinpoet.INT
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import it.unibo.collektive.test.LoopWithoutAlignTest.Companion.EXPECTED_WARNING_MESSAGE
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.PoetUtils.alignedOn
import it.unibo.collektive.test.util.PoetUtils.alignedOnS
import it.unibo.collektive.test.util.PoetUtils.block
import it.unibo.collektive.test.util.PoetUtils.blockS
import it.unibo.collektive.test.util.PoetUtils.nestedFunctionS
import it.unibo.collektive.test.util.PoetUtils.plus
import it.unibo.collektive.test.util.PoetUtils.shouldCompileWith
import it.unibo.collektive.test.util.PoetUtils.simpleAggregateFunction
import it.unibo.collektive.test.util.PoetUtils.simpleTestingFileWithAggregate
import it.unibo.collektive.test.util.PoetUtils.withFunction
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class IterationWithoutAlignTest : FreeSpec({
    "When using an iterative function like 'forEach' and similar" - {
        val sourceFile = simpleTestingFileWithAggregate()
        val startingFunction = simpleAggregateFunction(INT)

        // Note: "recoveryCode" is a piece of code to make the program compile whatever it is returned by the code
        // inside the anonymous function
        val testedIterativeFunctions = table(
            headers("function", "recoveryCode"),
            row("forEach", ""),
            row("filter", "false"),
            row("map", ""),
            row("flatMap", "listOf(null)"),
        )

        forAll(testedAggregateFunctions) { aggregateFunctionCall ->
            val functionName = aggregateFunctionCall.substringBefore("(")

            forAll(testedIterativeFunctions) { iterativeFunction, recoveryCode ->
                "using $functionName without a specific alignedOn" - {
                    val generated =
                        startingFunction + {
                            blockS("listOf(1,2,3).$iterativeFunction") {
                                """
                                    $aggregateFunctionCall
                                    $recoveryCode
                                    """
                            }
                        }
                    "should compile producing a warning" - {
                        sourceFile withFunction generated shouldCompileWith warning(
                            EXPECTED_WARNING_MESSAGE.format(functionName),
                        )
                    }
                }
                "using $functionName wrapped in a specific alignedOn" - {
                    val generated =
                        startingFunction + {
                            block("listOf(1,2,3).$iterativeFunction") {
                                alignedOnS("0") {
                                    """
                                        $aggregateFunctionCall
                                        $recoveryCode
                                        """
                                }
                            }
                        }
                    "should compile without any warning" - {
                        sourceFile withFunction generated shouldCompileWith noWarning
                    }
                }
                "using $functionName wrapped in a specific alignedOn outside the loop" - {
                    val generated =
                        startingFunction + {
                            alignedOn("0") {
                                blockS("listOf(1,2,3).$iterativeFunction") {
                                    """
                                    $aggregateFunctionCall
                                    $recoveryCode
                                    """
                                }
                            }
                        }
                    "should compile producing a warning" - {
                        sourceFile withFunction generated shouldCompileWith warning(
                            EXPECTED_WARNING_MESSAGE.format(functionName),
                        )
                    }
                }
                "using $functionName wrapped inside another function declaration" - {
                    val generated =
                        startingFunction + {
                            block("listOf(1,2,3).$iterativeFunction") {
                                nestedFunctionS("Aggregate<Int>.nested(): Unit") {
                                    aggregateFunctionCall
                                }.addCode(recoveryCode)
                            }
                        }
                    "should compile without any warning" - {
                        sourceFile withFunction generated shouldCompileWith noWarning
                    }
                }
            }
        }
    }
})
