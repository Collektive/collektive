package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class TestExplicitAlign : FreeSpec({
    val testingProgramTemplate = CompileUtils.testingProgramFromResource("SingleAggregateLine.kt")
    val expectedWarningMessage = "Warning: \"%s\" method should not be explicitly used"

    "An explicit align function should not be used" - {
        val testingProgram = testingProgramTemplate.formatCode("align(null)")
        "should produce a warning" - {
            testingProgram shouldCompileWith warning(expectedWarningMessage.format("align"))
        }
    }
    "An explicit dealign function should not be used" - {
        val testingProgram = testingProgramTemplate.formatCode("dealign()")
        "should produce a warning" - {
            testingProgram shouldCompileWith warning(expectedWarningMessage.format("dealign"))
        }
    }
})