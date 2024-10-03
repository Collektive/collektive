package it.unibo.collektive.test.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import it.unibo.collektive.AlignmentComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.util.HashMap

@OptIn(ExperimentalCompilerApi::class)
object CompileUtils {
    fun compile(fileName: String, program: String): JvmCompilationResult {
        val sourceFile = SourceFile.kotlin(fileName, program)

        return KotlinCompilation().apply {
            sources = listOf(sourceFile)
            compilerPluginRegistrars = listOf(AlignmentComponentRegistrar())
            inheritClassPath = true
        }.compile()
    }

    data class KotlinTestingProgram(
        val fileName: String,
        private val template: String,
        val program: String,
        private val properties: Map<String, String>,
    ) {

        fun formatCode(vararg args: Any?): KotlinTestingProgram =
            KotlinTestingProgram(fileName, template, program.format(*args), properties)

        fun put(key: String, value: String): KotlinTestingProgram {
            val updateProperties = properties + (key to value)
            return KotlinTestingProgram(
                fileName,
                template,
                StringSubstitutor.replace(template, updateProperties),
                updateProperties,
            )
        }

        fun import(javaClass: Class<*>): KotlinTestingProgram =
            put(
                "imports",
                properties["imports"] + "\nimport " + javaClass.name,
            )

        infix fun shouldCompileWith(compilationCheck: (JvmCompilationResult) -> Unit) {
            val result = compile(fileName, program)
            result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            compilationCheck(result)
        }
    }

    val noWarning: (JvmCompilationResult) -> Unit = { it.messages shouldNotContain "Warning" }

    fun warning(warningMessage: String): (JvmCompilationResult) -> Unit = { it.messages shouldContain warningMessage }

    fun testingProgramFromResource(fileName: String): KotlinTestingProgram {
        val content: String = checkNotNull(ClassLoader.getSystemClassLoader().getResource(fileName)).readText()
        return KotlinTestingProgram(fileName, content, content, HashMap())
    }

    enum class ProgramTemplates(val fileName: String, val defaultProperties: Map<String, String>) {
        SINGLE_AGGREGATE_LINE(
            "SingleAggregateLine.template.kt",
            mapOf(),
        ),
        SINGLE_AGGREGATE_IN_A_LOOP(
            "SingleAggregateInLoop.template.kt",
            mapOf(
                "aggregate" to "exampleAggregate()",
            ),
        ),
        SINGLE_AGGREGATE_IN_ITERATIVE_FUNCTION(
            "SingleAggregateInIterativeFunction.template.kt",
            mapOf(
                "aggregate" to "exampleAggregate()",
                "iterativeFunction" to "forEach",
            ),
        ),
    }

    fun testingProgramFromTemplate(template: ProgramTemplates): KotlinTestingProgram =
        testingProgramFromResource(template.fileName).copy(properties = template.defaultProperties).put("", "")

    object StringSubstitutor {
        fun replace(
            template: String,
            properties: Map<String, String>,
        ): String {
            return template.replace(Regex("%\\(([^)]+)\\)")) { matchResult ->
                val key = matchResult.groupValues[1]
                properties[key].orEmpty()
            }
        }
    }
}
