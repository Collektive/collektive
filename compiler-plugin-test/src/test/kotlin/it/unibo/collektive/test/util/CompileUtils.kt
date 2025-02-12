package it.unibo.collektive.test.util

import it.unibo.collektive.compiler.CollektiveK2JVMCompiler
import it.unibo.collektive.compiler.logging.CollectingMessageCollector
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.createTempDirectory
import kotlin.test.assertTrue

object CompileUtils {
    data class KotlinTestingProgram(
        val fileName: String,
        val program: String,
    ) {
        infix fun shouldCompileWith(compilationCheck: (CollectingMessageCollector) -> Unit) {
            val collector = CollectingMessageCollector()
            val tempDir =
                createTempDirectory("collektive-test").toFile().also {
                    require(it.exists() && it.isDirectory)
                }
            val programFile =
                tempDir.resolve(fileName).apply {
                    writeText(program)
                }
            CollektiveK2JVMCompiler.compile(listOf(programFile), collector)
            // Ensure no compilation errors (excluding "-Werror" warnings treated as errors)
            val errorMessages = collector[CompilerMessageSeverity.ERROR].filterNot { it.message.contains("-Werror") }
            assertTrue(errorMessages.isEmpty(), "Compilation errors found: $errorMessages")
            // Apply the provided compilation check
            compilationCheck(collector)
        }
    }

    val noWarning: (CollectingMessageCollector) -> Unit = {
        assertTrue(
            it[CompilerMessageSeverity.WARNING].isEmpty(),
            "Expected no warnings, but found: ${it[CompilerMessageSeverity.WARNING]}",
        )
    }

    fun warningMessage(warningMessage: String): (CollectingMessageCollector) -> Unit =
        { collector ->
            val warnings = collector[CompilerMessageSeverity.WARNING].map { it.message }
            assertTrue(
                warnings.contains(warningMessage),
                "Expected warning '$warningMessage' not found. Actual warnings: $warnings",
            )
        }

    fun String.asTestingProgram(fileName: String): KotlinTestingProgram = KotlinTestingProgram(fileName, this)

    fun pascalCase(vararg words: String): String = words.joinToString("") { it.replaceFirstChar(Char::titlecase) }

    /**
     * Retrieves a Kotlin testing program from a map of files given its [name].
     */
    fun Map<String, File>.getTestingProgram(name: String): KotlinTestingProgram =
        this[name]?.readText()?.asTestingProgram("$name.kt")
            ?: throw FileNotFoundException("Program not found: $name")

    internal fun Map<String, File>.subject(
        case: String,
        functionName: String,
        iteration: String,
    ) = getTestingProgram(pascalCase(case, functionName, iteration))

    internal fun Map<String, File>.`iteration with warning`(
        case: String,
        functionName: String,
        iteration: String,
        warning: String,
    ) {
        subject(case, functionName, iteration) shouldCompileWith warningMessage(warning)
    }

    internal fun Map<String, File>.`iteration without warning`(
        case: String,
        functionName: String,
        iteration: String,
    ) {
        subject(case, functionName, iteration) shouldCompileWith noWarning
    }

    val testedAggregateFunctions =
        listOf(
            "neighboring(0)",
        )

    val formsOfIteration =
        listOf(
            "For" to "a for loop",
            "ForEach" to "a 'forEach' call",
        )
}
