package it.unibo.collektive.test

import com.squareup.kotlinpoet.INT
import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.shouldCompileWith
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.PoetUtils.function
import it.unibo.collektive.test.util.PoetUtils.rem
import it.unibo.collektive.test.util.PoetUtils.simpleAggregateFunction
import it.unibo.collektive.test.util.PoetUtils.simpleTestingFileWithAggregate
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class ExplicitAlignTest : FreeSpec({
    val fileTemplate = simpleTestingFileWithAggregate()
    val functionTemplate = simpleAggregateFunction(INT)
    "The `align` function" - {
        "should produce a warning when used explicitly" - {
            fileTemplate % {
                function {
                    functionTemplate % { addCode("align(null)") }
                }
            } shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("align"))
        }
    }
    "The `dealign` function" - {
        "should produce a warning when used explicitly" - {
            fileTemplate % {
                function {
                    functionTemplate % { addCode("dealign()") }
                }
            } shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("dealign"))
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
    }
}
