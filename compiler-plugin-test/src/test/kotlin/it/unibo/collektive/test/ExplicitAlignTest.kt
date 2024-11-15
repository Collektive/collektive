package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.warning
import it.unibo.collektive.utils.common.AggregateFunctionNames
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class ExplicitAlignTest : FreeSpec({
    "The `align` function" - {
        val code = codeTemplate.format("align(null)").asTestingProgram("ExplicitAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith warning(
                EXPECTED_WARNING_MESSAGE.format(AggregateFunctionNames.ALIGN_FUNCTION_FQ_NAME),
            )
        }
    }
    "The `dealign` function" - {
        val code = codeTemplate.format("dealign()").asTestingProgram("ExplicitDeAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith warning(
                EXPECTED_WARNING_MESSAGE.format(AggregateFunctionNames.DEALIGN_FUNCTION_FQ_NAME),
            )
        }
    }
}) {
    companion object {
//        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
        const val EXPECTED_WARNING_MESSAGE = "The function '%s' should not be called explicitly"

        val codeTemplate = """
        import it.unibo.collektive.aggregate.api.Aggregate
        
        fun Aggregate<Int>.entry() {
            %s
        }
        """.trimIndent()
    }
}
