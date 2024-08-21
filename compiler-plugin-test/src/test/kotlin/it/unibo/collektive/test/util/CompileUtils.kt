package it.unibo.collektive.test.util

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
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

    data class KotlinTestingProgram(val fileName: String, val program: String) {

        fun formatCode(vararg args: Any?): KotlinTestingProgram =
            KotlinTestingProgram(fileName, program.format(*args))

        infix fun shouldCompileWith(compilationCheck: (JvmCompilationResult) -> Unit) {
            val result = compile(fileName, program)
            result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            compilationCheck(result)
        }
    }

    val noWarning: (JvmCompilationResult) -> Unit = { it.messages shouldNotContain "Warning" }

    fun warning(warningMessage: String): (JvmCompilationResult) -> Unit = { it.messages shouldContain warningMessage }

    fun testingProgramFromResource(fileName: String): KotlinTestingProgram =
        KotlinTestingProgram(
            fileName,
            checkNotNull(ClassLoader.getSystemClassLoader().getResource(fileName)).readText(),
        )
}
