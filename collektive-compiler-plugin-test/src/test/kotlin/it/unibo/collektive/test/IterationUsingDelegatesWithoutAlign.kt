/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toTempFiles
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.`iteration with warning`
import it.unibo.collektive.test.util.CompileUtils.`iteration without warning`
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import java.io.File
import kotlin.test.Test

class IterationUsingDelegatesWithoutAlign {
    private val testSubjects: Map<String, File> =
        subjekt {
            addSource("src/test/resources/subjekt/IterationWithDelegatedAggregate.yaml")
        }.toTempFiles()

    @Test
    fun `test iterations using delegates`() {
        for (functionCall in testedAggregateFunctions) {
            val functionName = functionCall.substringBefore("(")
            for ((iteration, _) in formsOfIteration) {
                with(testSubjects) {
                    `iteration with warning`("IterationDelegate", functionName, iteration, expectedWarning)
                    `iteration without warning`("IterationAlignDelegate", functionName, iteration)
                    `iteration with warning`("IterationDelegateAlign", functionName, iteration, expectedWarning)
                    `iteration with warning`("IterationDelegateWithNestedFun", functionName, iteration, expectedWarning)
                    `iteration with warning`("IterationRecursiveDelegate", functionName, iteration, expectedWarning)
                    `iteration with warning`(
                        "IterationAlignRecursiveDelegate",
                        functionName,
                        iteration,
                        expectedWarning,
                    )
                    `iteration with warning`(
                        "IterationRecursiveDelegateAlign",
                        functionName,
                        iteration,
                        expectedWarning,
                    )
                    // Disabled tests
                    // `iteration with warning`("IterationDelegatedNestedFun", functionName, iteration, expectedWarning)
                    // `iteration without warning`("IterationAlignDelegatedNestedFun", functionName, iteration)
                    // `iteration without warning`("IterationDelegatedNestedFunAlign", functionName, iteration)
                }
            }
        }
    }

    companion object {
        private val expectedWarning: String =
            """
            Aggregate function 'delegate' is called inside a loop without explicit alignment.  
            This may cause the same execution path to trigger multiple interactions, resulting in ambiguous alignment.
            Wrap the function call using 'alignedOn' with a unique key to ensure consistent alignment.
            """.trimIndent()
    }
}
