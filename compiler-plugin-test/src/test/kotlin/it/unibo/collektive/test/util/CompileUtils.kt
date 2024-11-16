package it.unibo.collektive.test.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import it.unibo.collektive.AlignmentComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

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
        val program: String,
    ) {

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
        return KotlinTestingProgram(fileName, content)
    }

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

    fun String.asTestingProgram(fileName: String): KotlinTestingProgram = KotlinTestingProgram(fileName, this)

    val testedAggregateFunctions = table(
        headers("functionCall"),
        row("neighboring(0)"),
    )

    val formsOfIteration = table(
        headers("iteration", "iterationDescription"),
        row("For", "a for loop"),
        row("ForEach", "a 'forEach' call"),
    )
}
