/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.compiler

import it.unibo.collektive.compiler.logging.SLF4JMessageCollector
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.common.setupCommonArguments
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.scripting.compiler.plugin.ScriptingK2CompilerPluginRegistrar
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.script.experimental.jvm.util.KotlinJars
import kotlin.script.experimental.jvm.util.classpathFromClassloader

/**
 * A facade for the Kotlin-JVM compiler with the Collektive plugin.
 */
@OptIn(ExperimentalCompilerApi::class)
@Deprecated(
    "This facade is deprecated since it does not execute the Collektive frontend compiler plugin," +
        " and uses unstable compiler APIs",
    ReplaceWith("CollektiveK2JVMCompiler.compile"),
)
object CollektiveJVMCompiler {
    private fun tempDir(module: String) = createTempDirectory(module).toFile()

    private const val FIRST_JAVA_VERSION_WITH_MODULE_SYSTEM = 9

    private val defaultJvmTarget =
        System.getProperty("java.version").substringBefore('.').toInt().let { version ->
            when {
                version < FIRST_JAVA_VERSION_WITH_MODULE_SYSTEM -> JvmTarget.JVM_1_8
                else -> JvmTarget.fromString(version.toString()) ?: JvmTarget.DEFAULT
            }
        }

    /**
     * Configures the Kotlin-JVM compiler to compile the [inputFiles] using the Collektive plugin.
     * When [inputFiles] contains a directory, it is used as a source root.
     */
    @JvmStatic
    @JvmOverloads
    fun compile(
        inputFiles: List<File>,
        jvmTarget: JvmTarget = defaultJvmTarget,
        moduleName: String =
            "collektive-${inputFiles.map { it.nameWithoutExtension }.sorted().joinToString("-")}",
        outputFolder: File = tempDir(moduleName),
        enableContextReceivers: Boolean = true,
        messageCollector: MessageCollector = SLF4JMessageCollector.default,
    ): GenerationState? {
        val configuration = CompilerConfiguration()
        // Input configuration
        inputFiles.forEach { file ->
            when {
                file.isDirectory -> configuration.addKotlinSourceRoot(file.absolutePath)
                else -> configuration.addKotlinSourceRoot(file.parentFile.absolutePath)
            }
        }
        // CLI compiler configuration
        configuration.put(CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY, messageCollector)
        // Common Kotlin configuration
        configuration.put(CommonConfigurationKeys.MODULE_NAME, moduleName)
        // Kotlin-JVM specific configuration
        configuration.put(JVMConfigurationKeys.JVM_TARGET, jvmTarget)
        configuration.put(JVMConfigurationKeys.OUTPUT_DIRECTORY, outputFolder)
        configuration.put(JVMConfigurationKeys.JDK_HOME, File(System.getProperty("java.home")))
        configuration.put(JVMConfigurationKeys.NO_JDK, false)
        // Enable context-receivers
        if (enableContextReceivers) {
            val config = K2JVMCompilerArguments().apply { contextReceivers = true }
            configuration.setupCommonArguments(config)
        }
        // Enable the IR backend, or the Collektive plugin cannot be applied
        configuration.put(CommonConfigurationKeys.USE_FIR, true)
        // Classpath configuration
        val classpath =
            checkNotNull(classpathFromClassloader(Thread.currentThread().contextClassLoader)) {
                "Empty classpath from current classloader." +
                    "Likely a bug in alchemist-incarnation-collective's Kotlin compiler facade"
            }
        configuration.addJvmClasspathRoots(classpath)
        configuration.addJvmClasspathRoots(KotlinJars.compilerClasspath)
        configuration.addJvmClasspathRoot(KotlinJars.stdlib)
        // Add the Collektive plugin
        configuration.add(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS, ScriptingK2CompilerPluginRegistrar())
        configuration.add(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS, AlignmentComponentRegistrar())
        // Configure the Collektive plugin options available in the command line processor
        configuration.put(AlignmentCommandLineProcessor.ARG_ENABLED, true)
        val environment =
            KotlinCoreEnvironment.createForProduction(
                { },
                configuration,
                EnvironmentConfigFiles.JVM_CONFIG_FILES,
            )
        return KotlinToJVMBytecodeCompiler.analyzeAndGenerate(environment)
    }

    /**
     * Configures the Kotlin-JVM compiler to compile the [inputFile] using the Collektive plugin.
     * When [inputFile] is a directory, it is used as a source root.
     */
    @JvmStatic
    @JvmOverloads
    fun compile(
        inputFile: File,
        jvmTarget: JvmTarget = defaultJvmTarget,
        moduleName: String = "collektive-${inputFile.nameWithoutExtension}",
        outputFolder: File = tempDir(moduleName),
        enableContextReceivers: Boolean = true,
        messageCollector: MessageCollector = SLF4JMessageCollector.default,
    ): GenerationState? = compile(
        listOf(inputFile),
        jvmTarget,
        moduleName,
        outputFolder,
        enableContextReceivers,
        messageCollector,
    )

    /**
     * Compiles the [input] string as a Kotlin-JVM file using the Collektive plugin.
     */
    @JvmStatic
    @JvmOverloads
    fun compileString(
        input: String,
        jvmTarget: JvmTarget = defaultJvmTarget,
        moduleName: String = "CollektiveScript",
        outputFolder: File = createTempDirectory(moduleName).toFile(),
        temporaryFolder: File = tempDir(moduleName),
        enableContextReceivers: Boolean = true,
        messageCollector: MessageCollector = SLF4JMessageCollector.default,
    ) = compile(
        temporaryFolder
            .also { require(it.exists() && it.isDirectory) }
            .resolve("$moduleName.kt")
            .also { it.writeText(input) },
        jvmTarget,
        moduleName,
        outputFolder,
        enableContextReceivers,
        messageCollector,
    )
}
