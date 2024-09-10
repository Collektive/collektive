package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.ProgramTemplates
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class LoopWithoutAlignTest : FreeSpec({
    "When being inside a loop in an Aggregate function" - {
        val testingProgramTemplate = CompileUtils.testingProgramFromTemplate(
            ProgramTemplates.SINGLE_AGGREGATE_IN_A_LOOP,
        )
        listOf(
            "exampleAggregate" to "exampleAggregate()",
            "neighboring" to "neighboring(0)",
        ).forEach { (functionName, functionCall) ->
            "using $functionName without a specific alignedOn" - {
                "should produce a warning" - {
                    val testingProgram = testingProgramTemplate
                        .put("aggregate", functionCall)
                    testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format(functionName))
                }
            }
            "using $functionName wrapped in a specific alignedOn" - {
                val testingProgram = testingProgramTemplate
                    .put("beforeAggregate", "alignedOn(0) {")
                    .put("afterAggregate", "}")
                "should compile without any warning" - {
                    testingProgram shouldCompileWith noWarning
                }
            }
            "using $functionName wrapped in a specific alignedOn outside the loop" - {
                val testingProgram = testingProgramTemplate
                    .put("beforeLoop", "alignedOn(0) {")
                    .put("afterLoop", "}")
                "should produce a warning" - {
                    val testingProgramWithCustomFunction = testingProgram
                        .put("aggregate", functionCall)
                    testingProgramWithCustomFunction shouldCompileWith warning(
                        EXPECTED_WARNING_MESSAGE.format(functionName),
                    )
                }
            }
            "using $functionName wrapped inside another function declaration" - {
                val testingProgram = testingProgramTemplate
                    .put("beforeAggregate", "fun Aggregate<Int>.test() {")
                    .put("afterAggregate", "}")
                "should compile without any warning" - {
                    testingProgram shouldCompileWith noWarning
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
