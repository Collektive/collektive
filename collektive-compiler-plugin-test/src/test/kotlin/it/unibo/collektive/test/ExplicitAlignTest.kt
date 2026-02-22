/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.compiler.common.CollektiveNames
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.warningMessage
import java.util.Locale

class ExplicitAlignTest : FreeSpec({
    "The `align` function" - {
        val code = codeTemplate.format(Locale.US, "align(null)").asTestingProgram("ExplicitAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith
                warningMessage(
                    EXPECTED_WARNING_MESSAGE.format(Locale.US, CollektiveNames.ALIGN_FUNCTION_FQ_NAME),
                )
        }
    }
    "The `dealign` function" - {
        val code = codeTemplate.format(Locale.US, "dealign()").asTestingProgram("ExplicitDeAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith
                warningMessage(
                    EXPECTED_WARNING_MESSAGE.format(Locale.US, CollektiveNames.DEALIGN_FUNCTION_FQ_NAME),
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
