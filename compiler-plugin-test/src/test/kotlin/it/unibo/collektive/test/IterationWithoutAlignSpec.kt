package it.unibo.collektive.test

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toTempFiles
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import it.unibo.collektive.test.util.CompileUtils.KotlinTestingProgram
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.getTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.pascalCase
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

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
                 * Gets the [KotlinTestingProgram] corresponding to a specific [case].
                 */
                fun getProgramFromCase(case: String): KotlinTestingProgram =
                    testSubjects.getTestingProgram(pascalCase(case, functionName, iteration))

                "inside $iterationDescription and using $functionName without a specific alignedOn" - {
                    val case = "Iteration"
                    val code = getProgramFromCase(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(functionName),
                            )
                    }
                }

                "inside $iterationDescription and using $functionName wrapped in a specific alignedOn" - {
                    val case = "IterationAlign"
                    val code = getProgramFromCase(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }

                "inside $iterationDescription and using $functionName wrapped in alignedOn outside the loop" - {
                    val case = "IterationExtAlign"
                    val code = getProgramFromCase(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith
                            warning(
                                expectedWarning(functionName),
                            )
                    }
                }

                "inside $iterationDescription and using $functionName wrapped inside another function declaration" - {
                    val case = "IterationWithNestedFun"
                    val code = getProgramFromCase(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }

                "inside $iterationDescription outside the 'aggregate' entry point while using $functionName" - {
                    val case = "OutsideAggregate"
                    val code = getProgramFromCase(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }
            }
        }
    }
})
