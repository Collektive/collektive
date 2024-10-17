package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.asTestingProgram
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class ExplicitAlignTest : FreeSpec({
    val codeTemplate = """
        import it.unibo.collektive.aggregate.api.Aggregate
        
        fun Aggregate<Int>.entry() {
            %s
        }
    """.trimIndent()
    "The `align` function" - {
        val code = codeTemplate.format("align(null)").asTestingProgram("ExplicitAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith warning(
                EXPECTED_WARNING_MESSAGE.format("align"),
            )
        }
    }
    "The `dealign` function" - {
        val code = codeTemplate.format("dealign()").asTestingProgram("ExplicitDeAlign.kt")
        "should produce a warning when used explicitly" - {
            code shouldCompileWith warning(
                EXPECTED_WARNING_MESSAGE.format("dealign"),
            )
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
    }
}
