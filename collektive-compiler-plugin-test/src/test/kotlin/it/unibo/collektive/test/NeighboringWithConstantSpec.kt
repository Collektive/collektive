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
import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.getTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warningMessage

class NeighboringWithConstantSpec : FreeSpec({

    /*
     * Test rationale:
     * The compiler plugin detects `neighboring(CONSTANT)` calls and emits a warning,
     * recommending the use of `mapNeighborhood { constant }` instead.
     *
     * These tests verify that:
     * 1. The warning IS emitted when a compile-time literal constant is passed to `neighboring`.
     *    - If the detection were missing, no warning would appear and the test would FAIL.
     * 2. The warning is NOT emitted for non-constant arguments (variables, function calls).
     *    - This ensures the checker does not produce false positives.
     *
     * The warning confirms the detection mechanism is working.
     * The actual IR optimization (replacing neighboring(const) with mapNeighborhood { const })
     * is separately verified via runtime tests in collektive-dsl.
     */

    fun expectedWarning(): String =
        """
        This 'neighboring' call uses a compile-time constant, which is shared with neighbors unnecessarily.
        Consider using 'mapNeighborhood { constant }' instead, which provides minimal communication by only sharing the alignment token.
        """.trimIndent()

    "When using neighboring with a constant" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/NeighboringWithConstant.yaml")
            }.toTempFiles()

        "passing an integer literal should produce a warning" - {
            val code = testSubjects.getTestingProgram("NeighboringWithIntConstant")
            code shouldCompileWith warningMessage(expectedWarning())
        }

        "passing a string literal should produce a warning" - {
            val code = testSubjects.getTestingProgram("NeighboringWithStringConstant")
            code shouldCompileWith warningMessage(expectedWarning())
        }

        "passing a boolean literal should produce a warning" - {
            val code = testSubjects.getTestingProgram("NeighboringWithBooleanConstant")
            code shouldCompileWith warningMessage(expectedWarning())
        }

        "passing a variable (non-constant) should not produce a warning" - {
            val code = testSubjects.getTestingProgram("NeighboringWithVariable")
            code shouldCompileWith noWarning
        }

        "passing a function call result (non-constant) should not produce a warning" - {
            val code = testSubjects.getTestingProgram("NeighboringWithFunctionCall")
            code shouldCompileWith noWarning
        }

        "using mapNeighborhood instead should not produce a warning" - {
            val code = testSubjects.getTestingProgram("NeighboringWithMapNeighborhood")
            code shouldCompileWith noWarning
        }
    }
})
