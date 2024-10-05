package it.unibo.collektive.test

import com.squareup.kotlinpoet.INT
import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.PoetUtils.plus
import it.unibo.collektive.test.util.PoetUtils.shouldCompileWith
import it.unibo.collektive.test.util.PoetUtils.simpleAggregateFunction
import it.unibo.collektive.test.util.PoetUtils.simpleTestingFileWithAggregate
import it.unibo.collektive.test.util.PoetUtils.withFunction
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class ExplicitAlignTest : FreeSpec({
    val sourceFile = simpleTestingFileWithAggregate()
    val startingFunction = simpleAggregateFunction(INT)
    "The `align` function" - {
        val generated = startingFunction + { addCode("align(null)") }
        "should produce a warning when used explicitly" - {
            sourceFile withFunction generated shouldCompileWith warning(
                EXPECTED_WARNING_MESSAGE.format("align"),
            )
        }
    }
    "The `dealign` function" - {
        val generated = startingFunction + { addCode("dealign()") }
        "should produce a warning when used explicitly" - {
            sourceFile withFunction generated shouldCompileWith warning(
                EXPECTED_WARNING_MESSAGE.format("dealign"),
            )
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
    }
}
