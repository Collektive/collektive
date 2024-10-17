package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class LoopWithoutAlignTest : FreeSpec({

    fun getTestingFile(case: String, iteration: String, aggregateFunction: String): String =
        LoopWithoutAlignTest::class.java
            .getResource("/kotlin/${case}_${iteration}_${aggregateFunction}.kt")!!
            .readText()

    "When being inside a loop in an Aggregate function" - {
        forAll(testedAggregateFunctions) { functionCall ->
            val functionName = functionCall.substringBefore("(")

            "using $functionName without a specific alignedOn" - {
                val case = "SIMPLE_IT"
                val code = getTestingFile(
                    case = case,
                    iteration = "for",
                    aggregateFunction = functionName,
                ).asTestingProgram("$functionName-${case}_for.kt")

                "should compile producing a warning" - {
                    code shouldCompileWith warning(
                        EXPECTED_WARNING_MESSAGE.format(functionName),
                    )
                }
            }
            "using $functionName wrapped in a specific alignedOn" - {
                val case = "SIMPLE_IT_ALGN"
                val code = getTestingFile(
                    case = case,
                    iteration = "for",
                    aggregateFunction = functionName,
                ).asTestingProgram("$functionName-${case}_for.kt")

                "should compile without any warning" - {
                    code shouldCompileWith noWarning
                }
            }
            "using $functionName wrapped in a specific alignedOn outside the loop" - {
                val case = "IT_EXT_ALGN"
                val code = getTestingFile(
                    case = case,
                    iteration = "for",
                    aggregateFunction = functionName,
                ).asTestingProgram("$functionName-${case}_for.kt")

                "should compile producing a warning" - {
                    code shouldCompileWith warning(
                        EXPECTED_WARNING_MESSAGE.format(functionName),
                    )
                }
            }
            "using $functionName wrapped inside another function declaration" - {
                val case = "IT_NST_FUN"
                val code = getTestingFile(
                    case = case,
                    iteration = "for",
                    aggregateFunction = functionName,
                ).asTestingProgram("$functionName-${case}_for.kt")

                "should compile without any warning" - {
                    code shouldCompileWith noWarning
                }
            }
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: aggregate function '%s' called inside a loop " +
                "with no manual alignment operation"
    }
}
