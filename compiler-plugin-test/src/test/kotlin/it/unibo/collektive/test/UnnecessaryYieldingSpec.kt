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

class UnnecessaryYieldingSpec : FreeSpec({

    fun expectedWarning(construct: String): String =
        """
        The yielding block inside the '$construct' construct may not be necessary for this use case, as the 
        expression that is exchanged is the same as the one yielded inside the 'yielding' block.
        
        Consider switching to the same construct without the 'yielding' block ('evolve', 'exchange' or
        'share').
        """.trimIndent()

    "When using a construct with yielding" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/UnnecessaryYieldingContext.yaml")
            }.toTempFiles()

        val constructs =
            table(
                headers("construct"),
                row("evolving"),
                row("exchanging"),
                row("sharing"),
            )

        forAll(constructs) { construct ->

            "performing a data exchange inside the '$construct' construct and yielding the same value" - {

                "with a simple expression" - {
                    val subjectName = "UnnecessaryYielding${construct.replaceFirstChar(Char::uppercase)}Simple"
                    val code = testSubjects.getTestingProgram(subjectName)

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(construct),
                            )
                    }
                }

                "with a complex expression" - {
                    val subjectName = "UnnecessaryYielding${construct.replaceFirstChar(Char::uppercase)}Complex"
                    val code = testSubjects.getTestingProgram(subjectName)

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(construct),
                            )
                    }
                }
            }

            "performing a data exchange inside the '$construct' construct and yielding a different value" - {
                val subjectName = "NecessaryYielding${construct.replaceFirstChar(Char::uppercase)}"
                val code = testSubjects.getTestingProgram(subjectName)

                "should compile without warnings" - {
                    code shouldCompileWith noWarning
                }
            }

            "performing a data exchange inside the '$construct' construct and yielding a different value with " +
                "a different type" - {
                    val subjectName = "NecessaryYieldingDifferentType${construct.replaceFirstChar(Char::uppercase)}"
                    val code = testSubjects.getTestingProgram(subjectName)

                    "should compile without warnings" - {
                        code shouldCompileWith noWarning
                    }
                }
        }
    }
})
