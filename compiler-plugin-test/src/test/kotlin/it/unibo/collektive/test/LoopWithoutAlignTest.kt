package it.unibo.collektive.test

import com.squareup.kotlinpoet.INT
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.forAll
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.PoetUtils.alignedOn
import it.unibo.collektive.test.util.PoetUtils.alignedOnS
import it.unibo.collektive.test.util.PoetUtils.loop
import it.unibo.collektive.test.util.PoetUtils.loopS
import it.unibo.collektive.test.util.PoetUtils.nestedFunctionS
import it.unibo.collektive.test.util.PoetUtils.plus
import it.unibo.collektive.test.util.PoetUtils.shouldCompileWith
import it.unibo.collektive.test.util.PoetUtils.simpleAggregateFunction
import it.unibo.collektive.test.util.PoetUtils.simpleTestingFileWithAggregate
import it.unibo.collektive.test.util.PoetUtils.withFunction
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class LoopWithoutAlignTest : FreeSpec({
    "When being inside a loop in an Aggregate function" - {
        val sourceFile = simpleTestingFileWithAggregate()
        val startingFunction = simpleAggregateFunction(INT)

        forAll(testedAggregateFunctions) { functionCall ->
            val functionName = functionCall.substringBefore("(")

            "using $functionName without a specific alignedOn" - {
                "should compile producing a warning" - {
                    val generated =
                        startingFunction + {
                            loopS {
                                functionCall
                            }
                        }
                    sourceFile withFunction generated shouldCompileWith warning(
                        EXPECTED_WARNING_MESSAGE.format(functionName),
                    )
                }
            }
            "using $functionName wrapped in a specific alignedOn" - {
                "should compile without any warning" - {
                    val generated =
                        startingFunction + {
                            loop {
                                alignedOnS("0") {
                                    functionCall
                                }
                            }
                        }
                    sourceFile withFunction generated shouldCompileWith noWarning
                }
            }
            "using $functionName wrapped in a specific alignedOn outside the loop" - {
                "should compile producing a warning" - {
                    val generated =
                        startingFunction + {
                            alignedOn("0") {
                                loopS {
                                    functionCall
                                }
                            }
                        }
                    sourceFile withFunction generated shouldCompileWith warning(
                        EXPECTED_WARNING_MESSAGE.format(functionName),
                    )
                }
            }
            "using $functionName wrapped inside another function declaration" - {
                "should compile without any warning" - {
                    val generated =
                        startingFunction + {
                            loop {
                                nestedFunctionS("Aggregate<Int>.nested(): Unit") {
                                    functionCall
                                }
                            }
                        }
                    sourceFile withFunction generated shouldCompileWith noWarning
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
