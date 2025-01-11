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
import it.unibo.collektive.test.util.CompileUtils.warning

class ImproperConstructSpec : FreeSpec({

    fun expectedWarning(construct: String): String =
        """
        The '$construct' can be replaced by using the more appropriate 'share' construct for this use case.
        """.trimIndent()

    "When using an Aggregate construct" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/ImproperConstruct.yaml")
            }.toTempFiles()

        "an improper use of the 'evolve' construct with implicit parameter".config(enabled = false) - {
            val subjectName = "ImproperUseEvolveImplicit"
            val code = testSubjects.getTestingProgram(subjectName)

            "should compile producing a warning" - {
                code shouldCompileWith
                    warning(
                        expectedWarning("evolve"),
                    )
            }
        }

        "an improper use of the 'evolve' construct with explicit parameter".config(enabled = false) - {
            val subjectName = "ImproperUseEvolveExplicit"
            val code = testSubjects.getTestingProgram(subjectName)

            "should compile producing a warning" - {
                code shouldCompileWith
                    warning(
                        expectedWarning("evolve"),
                    )
            }
        }

        "a proper use of the 'evolve' construct with implicit parameter" - {
            val subjectName = "ProperUseEvolveImplicit"
            val code = testSubjects.getTestingProgram(subjectName)

            "should compile without warnings" - {
                code shouldCompileWith noWarning
            }
        }

        "a proper use of the 'evolve' construct with explicit parameter" - {
            val subjectName = "ProperUseEvolveExplicit"
            val code = testSubjects.getTestingProgram(subjectName)

            "should compile without warnings" - {
                code shouldCompileWith noWarning
            }
        }
    }
})
