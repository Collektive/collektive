package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class TestLoopWithoutAlignWarning : FreeSpec({
    "A single aggregate function called inside a loop" - {
        val testingProgramTemplate = CompileUtils.testingProgramFromResource("SingleAggregateInLoop.template.kt")
        "without a specific alignedOn" - {
            val testingProgram = testingProgramTemplate.formatCode("", "", "", "")
            "should produce a warning" - {
                testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE)
            }
        }
        "with a specific alignedOn" - {
            val testingProgram = testingProgramTemplate.formatCode("", "alignedOn(pivot(localId)) {", "}", "")
            "should compile without any warning" - {
                testingProgram shouldCompileWith noWarning
            }
        }
        "with a specific alignedOn placed outside the loop" - {
            val testingProgram = testingProgramTemplate.formatCode("alignedOn(pivot(localId)) {", "", "", "}")
            "should produce a warning" - {
                testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE)
            }
        }
        "but wrapped inside another function declaration" - {
            val testingProgram = testingProgramTemplate.formatCode("", "fun Aggregate<Int>.test() {", "}", "")
            "should compile without any warning" - {
                testingProgram shouldCompileWith noWarning
            }
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: aggregate function \"exampleAggregate\" called inside a loop " +
            "with no manual alignment operation"
    }
}
