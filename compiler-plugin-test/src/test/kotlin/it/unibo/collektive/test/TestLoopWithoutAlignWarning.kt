package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.ProgramTemplates
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class TestLoopWithoutAlignWarning : FreeSpec({
    "A single aggregate function called inside a loop: " - {
        val testingProgramTemplate = CompileUtils.testingProgramFromTemplate(
            ProgramTemplates.SINGLE_AGGREGATE_IN_A_LOOP,
        )
        listOf(
            "exampleAggregate" to "exampleAggregate()",
            "neighboring" to "neighboring(0)",
        ).forEach {
            "${it.first} without a specific alignedOn" - {
                "should produce a warning" - {
                    val testingProgram = testingProgramTemplate
                        .put("aggregate", it.second)
                    testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format(it.first))
                }
            }
            "${it.first} with a specific alignedOn" - {
                val testingProgram = testingProgramTemplate
                    .put("beforeAggregate", "alignedOn(pivot(localId)) {")
                    .put("afterAggregate", "}")
                "should compile without any warning" - {
                    testingProgram shouldCompileWith noWarning
                }
            }
            "${it.first} with a specific alignedOn placed outside the loop" - {
                val testingProgram = testingProgramTemplate
                    .put("beforeLoop", "alignedOn(pivot(localId)) {")
                    .put("afterLoop", "}")
                "should produce a warning" - {
                    val testingProgramWithCustomFunction = testingProgram
                        .put("aggregate", it.second)
                    testingProgramWithCustomFunction shouldCompileWith warning(
                        EXPECTED_WARNING_MESSAGE.format(it.first),
                    )
                }
            }
            "${it.first} wrapped inside another function declaration" - {
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
        const val EXPECTED_WARNING_MESSAGE = "Warning: aggregate function \"%s\" called inside a loop " +
            "with no manual alignment operation"
    }
}
