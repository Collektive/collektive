package it.unibo.collektive.test

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toTempFiles
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.pascalCase
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.FileNotFoundException

@OptIn(ExperimentalCompilerApi::class)
class IterationWithoutAlignSpec : FreeSpec({
    fun expectedWarning(functionName: String): String =
        """
        Aggregate function '$functionName' has been called inside a loop construct without explicit alignment.
        The same path may generate interactions more than once, leading to ambiguous alignment.
        
        Consider to wrap the function into the 'alignedOn' method with a unique element.
        """.trimIndent()

    "When iterating an Aggregate function" - {
        val testSubjects =
            subjekt {
                addSource("src/test/resources/subjekt/IterationWithAggregate.yaml")
            }.toTempFiles()

        forAll(testedAggregateFunctions) { functionCall ->
            val functionName = functionCall.substringBefore("(")
            forAll(formsOfIteration) { iteration, iterationDescription ->
                /**
                 * Gets the text from a map of files, given its [caseName], and converts it to a
                 * [CompileUtils.KotlinTestingProgram].
                 */
                fun getTestingProgram(caseName: String): CompileUtils.KotlinTestingProgram =
                    testSubjects[pascalCase(caseName, functionName, iteration)]
                        ?.readText()
                        ?.asTestingProgram("$functionName-${caseName}_$iteration.kt")
                        ?: throw FileNotFoundException(
                            "Program not found: ${pascalCase(caseName, functionName, iteration)}",
                        )

                "inside $iterationDescription and using $functionName without a specific alignedOn" - {
                    val case = "Iteration"
                    val code = getTestingProgram(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(functionName),
                            )
                    }
                }

                "inside $iterationDescription and using $functionName wrapped in a specific alignedOn" - {
                    val case = "IterationAlign"
                    val code = getTestingProgram(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }

                "inside $iterationDescription and using $functionName wrapped in alignedOn outside the loop" - {
                    val case = "IterationExtAlign"
                    val code = getTestingProgram(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(functionName),
                            )
                    }
                }

                "inside $iterationDescription and using $functionName wrapped inside another function declaration" - {
                    val case = "IterationWithNestedFun"
                    val code = getTestingProgram(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }

                "inside $iterationDescription outside the 'aggregate' entry point while using $functionName" - {
                    val case = "OutsideAggregate"
                    val code = getTestingProgram(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }
            }
        }
    }
})
