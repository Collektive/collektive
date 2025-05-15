/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
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
import kotlin.test.Test

class IterationWithoutAlign {
    private val testSubjects =
        subjekt {
            addSource("src/test/resources/subjekt/IterationWithAggregate.yaml")
        }.toTempFiles()

    @Test
    fun `test iterations without align`() {
        for (functionCall in testedAggregateFunctions) {
            val functionName = functionCall.substringBefore("(")
            for ((iteration, _) in formsOfIteration) {
                with(testSubjects) {
                    `iteration with warning`("Iteration", functionName, iteration, expectedWarn(functionName))
                    `iteration without warning`("IterationAlign", functionName, iteration)
                    `iteration with warning`("IterationExtAlign", functionName, iteration, expectedWarn(functionName))
                    `iteration without warning`("IterationWithNestedFun", functionName, iteration)
                    `iteration without warning`("OutsideAggregate", functionName, iteration)
                }
            }
        }
    }

    private companion object {
        private fun expectedWarn(functionName: String): String =
            """
            Aggregate function '$functionName' is called inside a loop without explicit alignment.  
            This may cause the same execution path to trigger multiple interactions, resulting in ambiguous alignment.
            Wrap the function call using 'alignedOn' with a unique key to ensure consistent alignment.
            """.trimIndent()
    }
}
