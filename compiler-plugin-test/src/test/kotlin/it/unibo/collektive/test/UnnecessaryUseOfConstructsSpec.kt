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
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import it.unibo.collektive.test.util.CompileUtils.getTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning

class UnnecessaryUseOfConstructsSpec : FreeSpec({

    fun expectedWarning(construct: String): String =
        """
        The '$construct' construct may not be necessary for this use case, as the parameter of the provided anonymous 
        function is unused within its body.
        
        Consider using a different construct or eliminating the call altogether.
        """.trimIndent()

    "When using an Aggregate construct" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/UnnecessaryUseOfConstructs.yaml")
            }.toTempFiles()

        val constructs =
            table(
                headers("construct"),
                row("evolve"),
                row("exchange"),
                row("share"),
                row("neighboring"),
                row("evolving"),
                row("exchanging"),
                row("neighboringViaExchange"),
                row("sharing"),
            )

        forAll(constructs) { construct ->

            "an unused parameter inside a '$construct' construct call" - {
                val subjectName = "UnnecessaryUse${construct.replaceFirstChar(Char::uppercase)}"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile producing a warning" - {
                    code shouldCompileWith
                        warning(
                            expectedWarning(construct),
                        )
                }
            }

            "an implicit parameter used inside a '$construct' construct call" - {
                val subjectName = "NecessaryUseImplicitParameter${construct.replaceFirstChar(Char::uppercase)}"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile without warnings" - {
                    code shouldCompileWith noWarning
                }
            }

            "a shadowed implicit parameter used inside a '$construct' construct call" - {
                val subjectName =
                    "UnnecessaryUseShadowedImplicitParameter${construct.replaceFirstChar(Char::uppercase)}"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile producing a warning" - {
                    code shouldCompileWith
                        warning(
                            expectedWarning(construct),
                        )
                }
            }

            "an explicit parameter used inside a '$construct' construct call" - {
                val subjectName = "NecessaryUseExplicitParameter${construct.replaceFirstChar(Char::uppercase)}"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile without warnings" - {
                    code shouldCompileWith noWarning
                }
            }
        }
    }
})
