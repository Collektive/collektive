/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler

import it.unibo.collektive.compiler.logging.SLF4JMessageCollector
import org.jetbrains.kotlin.cli.common.ExitCode
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.arguments.validateArguments
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.jetbrains.kotlin.incremental.classpathAsList
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createTempDirectory
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.script.experimental.jvm.util.classpathFromClassloader

/**
 * A facade for the Kotlin-JVM compiler with the Collektive plugin.
 */
object CollektiveK2JVMCompiler {
    /**
     * Configures the Kotlin-JVM compiler arguments to compile using the Collektive plugin.
     * The [destinationFolder] is used to store the compiled classes.
     * The [verbose] flag enables verbose output.
     * The [allWarningsAsErrors] flag treats all warnings as errors.
     */
    fun collektiveDefaultCompilerArguments(
        destinationFolder: Path,
        verbose: Boolean = true,
        allWarningsAsErrors: Boolean = true,
    ): K2JVMCompilerArguments = K2JVMCompilerArguments()
        .apply {
            destination = destinationFolder.absolutePathString()
            this.verbose = verbose
            suppressWarnings = false
            this.allWarningsAsErrors = allWarningsAsErrors
            reportOutputFiles = true
            reportPerf = false
            languageVersion = "2.0"
            multiPlatform = false
            noCheckActual = true
            val classpath =
                checkNotNull(classpathFromClassloader(Thread.currentThread().contextClassLoader)) {
                    "Empty classpath from current classloader." +
                        "Likely a bug in alchemist-incarnation-collective's Kotlin compiler facade"
                }
            val pluginClasspath =
                classpath.filter {
                    it.absolutePath.contains("compiler-plugin${File.separator}")
                }
            classpathAsList = classpath
            noStdlib = true
            noReflect = true
            pluginClasspaths = pluginClasspath.map(File::getAbsolutePath).toTypedArray()
            pluginOptions =
                arrayOf(
                    "plugin:it.unibo.collektive.compiler-plugin:collektiveEnabled=true",
                )
        }

    /**
     * Configures the Kotlin-JVM compiler to compile the [sources] using the Collektive plugin.
     * The [messageCollector] is used to collect the compiler messages.
     * The [destinationFolder] is used to store the compiled classes.
     */
    fun compile(
        sources: List<File>,
        messageCollector: MessageCollector = SLF4JMessageCollector.default,
        destinationFolder: Path = createTempDirectory("collektive-compiler"),
        compilerArguments: K2JVMCompilerArguments = collektiveDefaultCompilerArguments(destinationFolder),
    ): ExitCode {
        val compiler = K2JVMCompiler()
        val args =
            compilerArguments.apply {
                freeArgs = sources.map(File::getAbsolutePath).distinct()
            }
        validateArguments(args.errors)?.let {
            error("Invalid CLI arguments: $it")
        }
        return compiler.exec(messageCollector, Services.EMPTY, args)
    }

    /**
     * Compiles the [source] file using the Collektive plugin.
     * The [messageCollector] is used to collect the compiler messages.
     * The [destinationFolder] is used to store the compiled classes.
     */
    fun compile(
        source: File,
        messageCollector: MessageCollector = SLF4JMessageCollector.default,
        destinationFolder: Path = createTempDirectory("collektive-compiler"),
        compilerArguments: K2JVMCompilerArguments = collektiveDefaultCompilerArguments(destinationFolder),
    ): ExitCode = compile(listOf(source), messageCollector, destinationFolder, compilerArguments)

    /**
     * Compiles the [program] string using the Collektive plugin.
     * The [messageCollector] is used to collect the compiler messages.
     * The [destinationFolder] is used to store the compiled classes.
     */
    fun compileString(
        program: String,
        module: String = "CollektiveScript",
        messageCollector: MessageCollector = SLF4JMessageCollector.default,
        destinationFolder: Path = createTempDirectory("collektive-compiler"),
        compilerArguments: K2JVMCompilerArguments = collektiveDefaultCompilerArguments(destinationFolder),
    ): ExitCode {
        val sourceFile =
            createTempFile(module, ".kt")
                .apply {
                    writeText(program)
                }.toFile()
        return compile(listOf(sourceFile), messageCollector, destinationFolder, compilerArguments)
    }
}
