/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.warningMessage
import it.unibo.collektive.utils.common.AggregateFunctionNames

class ExplicitAlignTest : FreeSpec({
    "The `align` function" - {
        val code = codeTemplate.format("align(null)").asTestingProgram("ExplicitAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith
                warningMessage(
                    EXPECTED_WARNING_MESSAGE.format(AggregateFunctionNames.ALIGN_FUNCTION_FQ_NAME),
                )
        }
    }
    "The `dealign` function" - {
        val code = codeTemplate.format("dealign()").asTestingProgram("ExplicitDeAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith
                warningMessage(
                    EXPECTED_WARNING_MESSAGE.format(AggregateFunctionNames.DEALIGN_FUNCTION_FQ_NAME),
                )
        }
    }
}) {
    companion object {
        //        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
        const val EXPECTED_WARNING_MESSAGE = "The function '%s' must not be called explicitly"

        val codeTemplate =
            """
            import it.unibo.collektive.aggregate.api.Aggregate
            
            fun Aggregate<Int>.entry() {
                %s
            }
            """.trimIndent()
    }
}
