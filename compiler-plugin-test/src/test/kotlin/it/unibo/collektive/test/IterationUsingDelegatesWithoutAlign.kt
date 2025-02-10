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
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.`iteration with warning`
import it.unibo.collektive.test.util.CompileUtils.`iteration without warning`
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import kotlin.test.Test

@OptIn(ExperimentalCompilerApi::class)
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
                    `iteration without warning`("IterationDelegateAlign", functionName, iteration)
                    `iteration without warning`("IterationDelegateWithNestedFun", functionName, iteration)
                    `iteration with warning`("IterationRecursiveDelegate", functionName, iteration, expectedWarning)
                    `iteration without warning`("IterationAlignRecursiveDelegate", functionName, iteration)
                    `iteration without warning`("IterationRecursiveDelegateAlign", functionName, iteration)
                    // Disabled tests
//                `iteration with warning`("IterationDelegatedNestedFun", functionName, iteration, expectedWarning)
//                `iteration without warning`("IterationAlignDelegatedNestedFun", functionName, iteration)
//                `iteration without warning`("IterationDelegatedNestedFunAlign", functionName, iteration)
                }
            }
        }
    }

    companion object {
        private val expectedWarning: String =
            """
            Function 'delegate', that accepts and uses an aggregate argument, has been called inside a loop construct 
            without explicit alignment.
            The same path may generate interactions more than once, leading to ambiguous alignment.
            
            Consider to wrap the function into the 'alignedOn' method with a unique element, either at the call site
            or inside the 'delegate' function declaration, wrapping the involved aggregate calls.
            """.trimIndent()
    }
}
