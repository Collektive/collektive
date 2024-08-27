package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.ProgramTemplates
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class TestExplicitAlign : FreeSpec({
    val testingProgramTemplate = CompileUtils.testingProgramFromTemplate(ProgramTemplates.SINGLE_AGGREGATE_LINE)

    "An explicit align function should not be used" - {
        val testingProgram = testingProgramTemplate.put("code", "align(null)")
        "should produce a warning" - {
            testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("align"))
        }
    }
    "An explicit dealign function should not be used" - {
        val testingProgram = testingProgramTemplate.put("code", "dealign()")
        "should produce a warning" - {
            testingProgram shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("dealign"))
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: \"%s\" method should not be explicitly used"
    }
}
