/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test.util

import io.github.subjekt.Subjekt
import it.unibo.collektive.compiler.CollektiveK2JVMCompiler
import it.unibo.collektive.compiler.logging.CollectingMessageCollector
import java.io.File
import java.io.FileNotFoundException
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.assertTrue
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity

internal object CompileUtils {
    data class KotlinTestingProgram(val fileName: String, val program: String) {

        private fun compile(): Pair<ExitCode, CollectingMessageCollector> {
            val collector = CollectingMessageCollector()
            val tempDir = createTempDirectory("collektive-test").toFile().also {
                require(it.exists() && it.isDirectory)
            }
            val programFile = tempDir.resolve(fileName).apply {
                writeText(program)
            }
            return CollektiveK2JVMCompiler.compile(listOf(programFile), collector) to collector
        }

        infix fun shouldCompileWith(compilationCheck: (CollectingMessageCollector) -> Unit) {
            // Ensure no compilation errors (excluding "-Werror" warnings treated as errors)
            val (_, collector) = compile()
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

    fun warningMessage(warningMessage: String): (CollectingMessageCollector) -> Unit = { collector ->
        val warnings = collector[CompilerMessageSeverity.WARNING].map { it.message }
        assertTrue(
            warnings.contains(warningMessage),
            "Expected warning '$warningMessage' not found. Actual warnings: $warnings",
        )
    }

    fun String.asTestingProgram(fileName: String): KotlinTestingProgram = KotlinTestingProgram(fileName, this)

    private fun pascalCase(vararg words: String): String =
        words.joinToString("") { it.replaceFirstChar(Char::titlecase) }

    /**
     * Resolves the Subjekt suite at [path], returning the code of each subject, indexed by subject name.
     * The code preamble of the suite configuration is prepended to the code of every subject.
     */
    fun subjects(path: String): Map<String, String> {
        val suite = checkNotNull(Subjekt.fromYaml(File(path).readText()).resolvedSuite) {
            "Unable to resolve the Subjekt suite at $path"
        }
        val preamble = suite.originalSuite.configuration.codePreamble
            .takeIf { it.isNotBlank() }
            ?.let { "$it\n" }
            .orEmpty()
        return suite.resolvedSubjects.associate { subject ->
            val name = checkNotNull(subject.name?.value?.castToString()?.value) {
                "Unnamed subject in $path"
            }
            val code = checkNotNull(subject.instances["code"]?.value?.castToString()?.value) {
                "Subject $name in $path has no code"
            }
            name to preamble + code
        }
    }

    /**
     * Retrieves a Kotlin testing program from a map of subject codes given its [name].
     */
    fun Map<String, String>.getTestingProgram(name: String): KotlinTestingProgram =
        this[name]?.asTestingProgram("$name.kt")
            ?: throw FileNotFoundException("Program not found: $name")

    private fun Map<String, String>.subject(case: String, functionName: String, iteration: String) =
        getTestingProgram(pascalCase(case, functionName, iteration))

    fun Map<String, String>.`iteration with warning`(
        case: String,
        functionName: String,
        iteration: String,
        warning: String,
    ) {
        subject(case, functionName, iteration) shouldCompileWith warningMessage(warning)
    }

    fun Map<String, String>.`iteration without warning`(case: String, functionName: String, iteration: String) {
        subject(case, functionName, iteration) shouldCompileWith noWarning
    }

    val testedAggregateFunctions = listOf("neighboring(1 - 1)")

    val formsOfIteration = listOf(
        "For" to "a for loop",
        "ForEach" to "a 'forEach' call",
    )

    fun compileResource(name: String): Pair<ExitCode, CollectingMessageCollector> {
        val collector = CollectingMessageCollector()
        val file = createTempFile(suffix = ".kt")
        file.writeText(ClassLoader.getSystemResource("it/unibo/collektive/test/$name.kt").readText())
        val exitCode = CollektiveK2JVMCompiler.compile(file.toFile(), collector)
        return exitCode to collector
    }
}
