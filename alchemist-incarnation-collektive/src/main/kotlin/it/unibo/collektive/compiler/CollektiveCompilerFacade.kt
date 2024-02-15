package it.unibo.collektive.compiler

import it.unibo.collektive.AlignmentCommandLineProcessor
import it.unibo.collektive.AlignmentComponentRegistrar
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.config.addKotlinSourceRoot
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.EXCEPTION
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.INFO
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.LOGGING
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.OUTPUT
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.STRONG_WARNING
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSourceLocation
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoot
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.JVMConfigurationKeys
import org.jetbrains.kotlin.config.JvmTarget
import org.jetbrains.kotlin.scripting.compiler.plugin.ScriptingK2CompilerPluginRegistrar
import org.slf4j.LoggerFactory
import java.io.File
import kotlin.io.path.createTempDirectory
import kotlin.io.path.pathString
import kotlin.script.experimental.jvm.util.KotlinJars
import kotlin.script.experimental.jvm.util.classpathFromClassloader

private val logger = LoggerFactory.getLogger("Collektive Compiler")
private val classLoader = Thread.currentThread().getContextClassLoader()
// private val entryPointRegex = Regex("collektive\\s*\\s*(\\{(.*)})", RegexOption.DOT_MATCHES_ALL)

// interface CollektiveSnippetForAlchemist {
//    fun run(context: Aggregate<Int>, device: CollektiveDevice): Any?
// }

/**
 * Compile a Collektive program and return the result of the execution.
 */
@OptIn(ExperimentalCompilerApi::class)
fun compileCollektiveProgram(program: String) {
    println(program)
    val configuration = CompilerConfiguration()
    configuration.put(CommonConfigurationKeys.MODULE_NAME, "alchemist-${System.currentTimeMillis()}")
    configuration.put(
        JVMConfigurationKeys.JVM_TARGET,
        JvmTarget.fromString(Runtime.version().version().get(0).toString()) ?: JvmTarget.JVM_17,
    )
    configuration.put(
        CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
        object : MessageCollector {
            override fun clear() = Unit
            override fun hasErrors(): Boolean = false
            override fun report(
                severity: CompilerMessageSeverity,
                message: String,
                location: CompilerMessageSourceLocation?,
            ) {
                val logOperation: (String, Array<Any?>) -> Unit = when (severity) {
                    ERROR, EXCEPTION -> logger::error
                    STRONG_WARNING -> logger::warn
                    WARNING -> logger::warn
                    OUTPUT, INFO -> logger::info
                    LOGGING -> logger::debug
                }
                when (location) {
                    null -> logOperation("{} {}", arrayOf(severity, message))
                    else -> logOperation("{} {} at {}", arrayOf(severity, message, location))
                }
            }
        },
    )
    val temporaryDirectory = createTempDirectory("alchemist-collektive-compiler")
    println(temporaryDirectory.pathString)
    configuration.put(JVMConfigurationKeys.OUTPUT_DIRECTORY, temporaryDirectory.toFile())
    val classpath = checkNotNull(classpathFromClassloader(classLoader)) {
        "Empty classpath from current classloader." +
            "Likely a bug in alchemist-incarnation-collective's Kotlin compiler facade"
    }
    val javaHome = File(System.getProperty("java.home"))
    configuration.put(JVMConfigurationKeys.JDK_HOME, javaHome)
    configuration.addJvmClasspathRoots(classpath)
    configuration.addJvmClasspathRoots(KotlinJars.compilerClasspath)
    configuration.addJvmClasspathRoot(KotlinJars.stdlib)
    temporaryDirectory.resolve("source.kt").toFile().writeText(
        """
            import it.unibo.collektive.aggregate.api.Aggregate
            import it.unibo.collektive.aggregate.api.operators.*
            fun Aggregate<Int>.myTest(): Unit = when(localId) {
                in 1..10 -> println(neighboringViaExchange("ciao"))
                else -> println(neighboringViaExchange("miao"))
            }
        """.trimIndent(),
    )
    configuration.addKotlinSourceRoot(temporaryDirectory.pathString)
    // Enable the IR backend, or the Collektive plugin cannot be applied
    configuration.put(JVMConfigurationKeys.IR, true)
    // Add the Collektive plugin
    configuration.add(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS, ScriptingK2CompilerPluginRegistrar())
    configuration.add(CompilerPluginRegistrar.COMPILER_PLUGIN_REGISTRARS, AlignmentComponentRegistrar())
    // Configure the Collektive plugin options available in the command line processor
    configuration.put(AlignmentCommandLineProcessor.ARG_ENABLED, true)
    val environment = KotlinCoreEnvironment.createForProduction(
        Disposable { },
        configuration,
        EnvironmentConfigFiles.JVM_CONFIG_FILES,
    )
    val state = KotlinToJVMBytecodeCompiler.analyzeAndGenerate(environment)
    println(state)
}

/**
 * Compile a Collektive program and return the result of the execution.
 */
fun main() {
    compileCollektiveProgram("ciao")
}
