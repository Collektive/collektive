package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

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
                    val case = "SIMPLE_IT"
                    val code = getTestingProgram(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith warning(
                            EXPECTED_WARNING_MESSAGE.format(functionName),
                        )
                    }
                }

                "inside $iterationDescription and using $functionName wrapped in a specific alignedOn" - {
                    val case = "SIMPLE_IT_ALGN"
                    val code = getTestingProgram(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }

                "inside $iterationDescription and using $functionName wrapped in alignedOn outside the loop" - {
                    val case = "IT_EXT_ALGN"
                    val code = getTestingProgram(case)

                    "should compile producing a warning" - {
                        code shouldCompileWith warning(
                            EXPECTED_WARNING_MESSAGE.format(functionName),
                        )
                    }
                }

                "inside $iterationDescription and using $functionName wrapped inside another function declaration" - {
                    val case = "IT_NST_FUN"
                    val code = getTestingProgram(case)

                    "should compile without any warning" - {
                        code shouldCompileWith noWarning
                    }
                }

                "inside $iterationDescription outside the 'aggregate' entry point while using $functionName" - {
                    val case = "OUTSIDE_AGGREGATE"
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
        const val EXPECTED_WARNING_MESSAGE = "Warning: aggregate function '%s' called inside a loop " +
            "without explicit alignment"

        fun getTextFromResource(case: String, iteration: String, aggregateFunction: String): String =
            IterationWithoutAlignSpec::class.java
                .getResource(
                    "/kotlin/" +
                        case.replaceFirstChar(Char::titlecase) +
                        iteration.replaceFirstChar(Char::titlecase) +
                        "${aggregateFunction.replaceFirstChar(Char::titlecase)}.kt",
                )!!
                .readText()

        val formsOfIteration = table(
            headers("iteration", "iterationDescription"),
            row("for", "a for loop"),
            row("listOf_forEach", "a 'forEach' call"),
        )
    }
}
