package it.unibo.collektive.test.util

import io.kotest.data.headers
import io.kotest.data.row
import io.kotest.data.table
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import it.unibo.collektive.compiler.CollektiveK2JVMCompiler
import it.unibo.collektive.compiler.logging.CollectingMessageCollector
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.createTempDirectory

@OptIn(ExperimentalCompilerApi::class)
object CompileUtils {
    data class KotlinTestingProgram(
        val fileName: String,
        val program: String,
    ) {
        infix fun shouldCompileWith(compilationCheck: (CollectingMessageCollector) -> Unit) {
            val collector = CollectingMessageCollector()
            val program =
                createTempDirectory("collektive-test")
                    .toFile()
                    .also { require(it.exists() && it.isDirectory) }
                    .resolve(fileName)
                    .also { it.writeText(program) }
            CollektiveK2JVMCompiler.compile(listOf(program), collector)
            compilationCheck(collector)
        }
    }

    val noWarning: (CollectingMessageCollector) -> Unit = { it[CompilerMessageSeverity.WARNING].shouldBeEmpty() }

    fun warning(warningMessage: String): (CollectingMessageCollector) -> Unit =
        { collector ->
            collector[CompilerMessageSeverity.WARNING]
                .map { it.message }
                .any { it.contains(warningMessage) } shouldBe true
        }

    fun String.asTestingProgram(fileName: String): KotlinTestingProgram = KotlinTestingProgram(fileName, this)

    fun pascalCase(vararg words: String): String = words.joinToString("") { it.replaceFirstChar(Char::titlecase) }

    /**
     * Gets the text from a map of files, given its [name], and converts it to a
     * [KotlinTestingProgram].
     */
    fun Map<String, File>.getTestingProgram(name: String): KotlinTestingProgram =
        this[name]
            ?.readText()
            ?.asTestingProgram("$name.kt")
            ?: throw FileNotFoundException(
                "Program not found: $name",
            )

    val testedAggregateFunctions =
        table(
            headers("functionCall"),
            row("neighboring(0)"),
        )

    val formsOfIteration =
        table(
            headers("iteration", "iterationDescription"),
            row("For", "a for loop"),
            row("ForEach", "a 'forEach' call"),
        )
}
