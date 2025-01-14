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
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import it.unibo.collektive.test.util.CompileUtils.getTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning

class ImproperConstructSpec : FreeSpec({

    fun expectedWarning(construct: String): String =
        """
        The '$construct' construct can be replaced with a simpler `share` construct call. 
        """.trimIndent()

    "When using an Aggregate construct" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/ImproperConstruct.yaml")
            }.toTempFiles()

        val constructs =
            table(
                headers("construct"),
                row("evolve"),
                row("evolving"),
            )

        forAll(constructs) { construct ->
            "an improper use of the '$construct' construct with implicit parameter" - {
                val subjectName = "ImproperUse${construct.replaceFirstChar(Char::uppercase)}Implicit"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile producing a warning" - {
                    code shouldCompileWith
                        warning(
                            expectedWarning(construct),
                        )
                }
            }

            "an improper use of the '$construct' construct with explicit parameter" - {
                val subjectName = "ImproperUse${construct.replaceFirstChar(Char::uppercase)}Explicit"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile producing a warning" - {
                    code shouldCompileWith
                        warning(
                            expectedWarning(construct),
                        )
                }
            }

            "an improper use of the '$construct' construct with implicit parameter and delegated field" - {
                val subjectName = "ImproperUse${construct.replaceFirstChar(Char::uppercase)}DelegatedFieldImplicit"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile producing a warning" - {
                    code shouldCompileWith
                        warning(
                            expectedWarning(construct),
                        )
                }
            }

            "an improper use of the '$construct' construct with explicit parameter and delegated field" - {
                val subjectName = "ImproperUse${construct.replaceFirstChar(Char::uppercase)}DelegatedFieldExplicit"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile producing a warning" - {
                    code shouldCompileWith
                        warning(
                            expectedWarning(construct),
                        )
                }
            }

            "a proper use of the '$construct' construct with implicit parameter" - {
                val subjectName = "ProperUse${construct.replaceFirstChar(Char::uppercase)}Implicit"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile without warnings" - {
                    code shouldCompileWith noWarning
                }
            }

            "a proper use of the '$construct' construct with explicit parameter" - {
                val subjectName = "ProperUse${construct.replaceFirstChar(Char::uppercase)}Explicit"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile without warnings" - {
                    code shouldCompileWith noWarning
                }
            }

            "a proper use of the '$construct' construct where neighboring and evolve return the same value".config(
                enabled = false,
            ) -
                {
                    val subjectName = "ProperUse${construct.replaceFirstChar(Char::uppercase)}SameReturn"
                    val code = testSubjects.getTestingProgram(subjectName)

                    "should compile without warnings" - {
                        code shouldCompileWith noWarning
                    }
                }
        }
    }
})
