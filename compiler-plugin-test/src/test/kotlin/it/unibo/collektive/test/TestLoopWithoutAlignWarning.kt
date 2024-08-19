package it.unibo.collektive.test

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import it.unibo.collektive.AlignmentComponentRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class TestLoopWithoutAlignWarning : FreeSpec({
    "A single aggregate function called inside another one" - {
        val fileName = "TestAggregateInLoop.kt"
        val program = checkNotNull(ClassLoader.getSystemClassLoader().getResource(fileName)).readText()
        val sourceFile = SourceFile.kotlin(fileName, program)
        "should compile" - {
            val result = KotlinCompilation().apply {
                sources = listOf(sourceFile)
                compilerPluginRegistrars = listOf(AlignmentComponentRegistrar())
                inheritClassPath = true
            }.compile()
            val expectedWarningMessage = "Warning: aggregate function \"exampleAggregate\" called inside a loop " +
                "with no manual alignment operation"

            result.exitCode shouldBe KotlinCompilation.ExitCode.OK
            result.messages shouldContain expectedWarningMessage
        }
    }
})
