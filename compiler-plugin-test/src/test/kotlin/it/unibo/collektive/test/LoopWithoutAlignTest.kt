package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.ProgramTemplates
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.KotlinTestingProgramDsl.fillWith
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class LoopWithoutAlignTest : FreeSpec({
    "When being inside a loop in an Aggregate function" - {
        val template = CompileUtils.testingProgramFromTemplate(
            ProgramTemplates.SINGLE_AGGREGATE_IN_A_LOOP,
        )
        listOf(
            "exampleAggregate" to "exampleAggregate()",
            "neighboring" to "neighboring(0)",
        ).forEach { (functionName, functionCall) ->
            "using $functionName without a specific alignedOn" - {
                "should produce a warning" - {
                    template fillWith {
                        functionCall inside "mainCode"
                    } shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format(functionName))
                }
            }
            "using $functionName wrapped in a specific alignedOn" - {
                "should compile without any warning" - {
                    template fillWith {
                        "alignedOn(pivot(localId))" wrapping {
                            "mainCode"
                        }
                    } shouldCompileWith noWarning
                }
            }
            "using $functionName wrapped in a specific alignedOn outside the loop" - {
                template fillWith {
                    "alignedOn(pivot(localId))" wrapping {
                        "loop"
                    }
                    functionCall inside "mainCode"
                } shouldCompileWith warning(
                    EXPECTED_WARNING_MESSAGE.format(functionName),
                )
            }
            "using $functionName wrapped inside another function declaration" - {
                "should compile without any warning" - {
                    template fillWith {
                        "fun Aggregate<Int>.test()" wrapping {
                            "mainCode"
                        }
                    } shouldCompileWith noWarning
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
