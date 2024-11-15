package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import it.unibo.collektive.frontend.checkers.NoAlignInsideLoop
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.FileNotFoundException

@OptIn(ExperimentalCompilerApi::class)
class IterationWithoutAlignSpec : FreeSpec({

    "When iterating an Aggregate function" - {
        forAll(testedAggregateFunctions) { functionCall ->
            val functionName = functionCall.substringBefore("(")
            forAll(formsOfIteration) { iteration, iterationDescription ->
                /**
                 * Gets the text of a testing resource, given its [caseName], and converts it to a
                 * [CompileUtils.KotlinTestingProgram].
                 */
                fun getTestingProgram(caseName: String): CompileUtils.KotlinTestingProgram =
                    getTextFromResource(
                        case = caseName,
                        iteration = iteration,
                        aggregateFunction = functionName,
                    ).asTestingProgram("$functionName-${caseName}_$iteration.kt")

                "inside $iterationDescription and using $functionName without a specific alignedOn" - {
                    val case = "Iteration"
                    val code = getTestingProgram(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith warning(
                            NoAlignInsideLoop.createWarning(functionName),
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
                        code shouldCompileWith warning(
                            NoAlignInsideLoop.createWarning(functionName),
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

                "inside $iterationDescription and using a function that takes an Aggregate argument" - {
                    val case = "IterationDelegate"
                    val code = getTestingProgram(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                        /*warning(
                            "Warning: suspicious call of function '$functionName' with aggregate argument " +
                            "inside a loop with no manual alignment operation"
                        )*/
                    }
                }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "wrapped in a specific alignedOn" - {
                        val case = "IterationAlignDelegate"
                        val code = getTestingProgram(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }

                "inside $iterationDescription and using a function that takes an Aggregate argument " +
                    "with a specific alignedOn inside the called function" - {
                        val case = "IterationDelegateAlign"
                        val code = getTestingProgram(case)

                        "should compile without any warning" - {
                            code shouldCompileWith noWarning
                        }
                    }
            }
        }
    }
}) {
    companion object {

        fun getTextFromResource(case: String, iteration: String, aggregateFunction: String): String =
            IterationWithoutAlignSpec::class.java
                .getResource(
                    "/kotlin/" +
                        case.replaceFirstChar(Char::titlecase) +
                        aggregateFunction.replaceFirstChar(Char::titlecase) +
                        "${iteration.replaceFirstChar(Char::titlecase)}.kt",
                )?.readText()
                ?: throw FileNotFoundException(
                    "File not found for: case=$case, iteration=$iteration, aggregateFunction=$aggregateFunction",
                )

        val formsOfIteration = table(
            headers("iteration", "iterationDescription"),
            row("For", "a for loop"),
            row("ForEach", "a 'forEach' call"),
        )
    }
}
