package it.unibo.collektive.test

import com.squareup.kotlinpoet.INT
import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.shouldCompileWith
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.PoetUtils.alignedOn
import it.unibo.collektive.test.util.PoetUtils.loop
import it.unibo.collektive.test.util.PoetUtils.rem
import it.unibo.collektive.test.util.PoetUtils.simpleAggregateFunction
import it.unibo.collektive.test.util.PoetUtils.simpleTestingFileWithAggregate
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class LoopWithoutAlignTest : FreeSpec({
    "When being inside a loop in an Aggregate function" - {
        val fileTemplate = simpleTestingFileWithAggregate()
        val functionTemplate = simpleAggregateFunction(INT)

        listOf(
            "exampleAggregate" to "exampleAggregate()",
            "neighboring" to "neighboring(0)",
        ).forEach { (functionName, functionCall) ->
            "using $functionName without a specific alignedOn" - {
                "should produce a warning" - {
                    fileTemplate % {
                        addFunction(
                            functionTemplate % {
                                loop {
                                    addCode(functionCall)
                                }
                            },
                        )
                    } shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format(functionName))
                }
            }
            "using $functionName wrapped in a specific alignedOn" - {
                "should compile without any warning" - {
                    fileTemplate % {
                        addFunction(
                            functionTemplate % {
                                loop {
                                    alignedOn("0") {
                                        addCode(functionCall)
                                    }
                                }
                            },
                        )
                    } shouldCompileWith noWarning
                }
            }
            "using $functionName wrapped in a specific alignedOn outside the loop" - {
                fileTemplate % {
                    addFunction(
                        functionTemplate % {
                                    alignedOn("0") {
                                        loop {
                                            addCode(functionCall)
                                        }
                                    }
                                },
                    )
                } shouldCompileWith warning(
                    EXPECTED_WARNING_MESSAGE.format(functionName),
                )
            }
            "using $functionName wrapped inside another function declaration" - {
                "should compile without any warning" - {
                    fileTemplate % {
                        addFunction(
                            functionTemplate % {
                                        loop {
                                            beginControlFlow("fun Aggregate<Int>.nested(): Unit")
                                                .addCode(functionCall)
                                            .endControlFlow()
                                        }
                                    },
                        )
                    } shouldCompileWith noWarning
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
