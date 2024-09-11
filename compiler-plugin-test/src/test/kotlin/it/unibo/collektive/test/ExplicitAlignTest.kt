package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.ProgramTemplates
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.test.util.KotlinTestingProgramDsl.fillWith
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class ExplicitAlignTest : FreeSpec({
    val template = CompileUtils.testingProgramFromTemplate(ProgramTemplates.SINGLE_AGGREGATE_LINE)

    "The `align` function" - {
        "should produce a warning when used explicitly" - {
            template fillWith {
                "align(null)" inside "mainCode"
            } shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("align"))
        }
    }
    "The `dealign` function" - {
        "should produce a warning when used explicitly" - {
            template fillWith {
                "dealign()" inside "mainCode"
            } shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("dealign"))
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
    }
}
