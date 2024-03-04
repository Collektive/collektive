package it.unibo.alchemist

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.benmanes.caffeine.cache.LoadingCache
import it.unibo.alchemist.actions.RunCollektiveProgram
import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.Action
import it.unibo.alchemist.model.Actionable
import it.unibo.alchemist.model.Condition
import it.unibo.alchemist.model.Context
import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Incarnation
import it.unibo.alchemist.model.Molecule
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Reaction
import it.unibo.alchemist.model.TimeDistribution
import it.unibo.alchemist.model.conditions.AbstractCondition
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.nodes.GenericNode
import it.unibo.alchemist.model.reactions.Event
import it.unibo.alchemist.model.timedistributions.DiracComb
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.util.RandomGenerators.nextDouble
import it.unibo.collektive.compiler.CollektiveJVMCompiler
import it.unibo.collektive.compiler.logging.CollectingMessageCollector
import it.unibo.collektive.compiler.util.md5
import it.unibo.collektive.compiler.util.toBase32
import it.unibo.collektive.compiler.util.toBase64
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.ERROR
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.EXCEPTION
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.INFO
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.LOGGING
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.OUTPUT
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.STRONG_WARNING
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity.WARNING
import org.jetbrains.kotlin.codegen.state.GenerationState
import org.slf4j.LoggerFactory
import java.io.File
import java.lang.reflect.Method
import java.net.URLClassLoader
import javax.script.ScriptEngineManager
import kotlin.io.path.createTempDirectory
import kotlin.reflect.KProperty
import kotlin.reflect.full.starProjectedType

/**
 * Collektive incarnation in Alchemist.
 */
class CollektiveIncarnation<P> : Incarnation<Any?, P> where P : Position<P> {
    override fun getProperty(node: Node<Any?>, molecule: Molecule, property: String?): Double {
        val interpreted = when (property.isNullOrBlank()) {
            true -> node.getConcentration(molecule)
            else -> {
                val concentration = node.getConcentration(molecule)
                val concentrationType = when (concentration) {
                    null -> "Any?"
                    else -> {
                        val type = concentration::class.starProjectedType
                        "$type${"?".takeIf { type.isMarkedNullable }.orEmpty()}"
                    }
                }
                val toInvoke = propertyCache.get(
                    "import kotlin.math.*; val x: ($concentrationType) -> Any? = { $property }; x",
                )
                toInvoke(concentration)
            }
        }
        return when (interpreted) {
            is Double -> interpreted
            is Number -> interpreted.toDouble()
            is String -> interpreted.toDoubleOrNull() ?: Double.NaN
            else -> Double.NaN
        }
    }

    override fun createMolecule(molecule: String) = SimpleMolecule(molecule)

    override fun createConcentration(concentration: String?) = concentration

    override fun createConcentration() = Unit

    override fun createAction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        node: Node<Any?>?,
        time: TimeDistribution<Any?>,
        actionable: Actionable<Any?>,
        additionalParameters: Any?,
    ): Action<Any?> {
        requireNotNull(node) { "Collektive requires a device and cannot execute in a Global Reaction" }
        if (additionalParameters is CharSequence) {
            return RunCollektiveProgram(node, additionalParameters.toString())
        }
        val parameters = additionalParameters as? Map<*, *>
        requireNotNull(parameters) {
            val type = additionalParameters?.let { it::class.simpleName } ?: "null"
            "Invalid parameters for Collektive. Map required, but $type has been provided: $additionalParameters"
        }
        val entrypoint: String = requireNotNull(parameters["entrypoint"]) {
            "No entrypoint provided in $additionalParameters"
        }.toString()
        val code: String = parameters["code"]?.toString().orEmpty()
        val sourceSets: List<File> = parameters["source-sets"].toFiles()
        val classpath = sourceSets.joinToString(separator = File.pathSeparator) { it.absolutePath }
        val internalIdentifier = "$classpath$code$entrypoint".md5().toBase32()
        val name: String = parameters["name"]?.toString()?.replaceFirstChar { it.lowercase() }
            ?: "collektive$internalIdentifier"
        check(name.matches(validName)) {
            "Invalid name for Collektive program: $name"
        }
        val className = name.replaceFirstChar { it.uppercase() }
        val packageName = findPackage.find(code)?.groupValues?.get(1).orEmpty()
        val classFqName = "$packageName.$className"
        val classLoader = classLoaders.get("$name$internalIdentifier")
        fun loadMethod() = classLoader.loadClass(classFqName).methods.first { it.name == name }
        val methodToCall: Method = runCatching {
            loadMethod()
        }.recover { exception ->
            logger.info("Collektive program $name not found, compiling", exception)
            val inputFolder = createTempDirectory("collektive").toFile()
            val finalCode = """
                |@file:JvmName("$className")
                |${code.replace("\n", "\n|")}
                |context(CollektiveDevice<P>)
                |fun <P : Position<P>> Aggregate<Int>.$name() = $entrypoint
                """.trimMargin()
            inputFolder.resolve("$className.kt").writeText(finalCode)
            val outputFolder = File(classLoader.urLs.first().file)
            check(outputFolder.exists() && outputFolder.isDirectory) {
                "Output folder ${outputFolder.absolutePath} does not exist or is not a directory"
            }
            val messages = CollectingMessageCollector()
            val result: GenerationState? = CollektiveJVMCompiler.compile(
                sourceSets + inputFolder,
                moduleName = name,
                outputFolder = outputFolder,
                messageCollector = messages,
            )
            messages.messages.forEach { (severity, message) ->
                when (severity) {
                    ERROR, EXCEPTION -> logger.error(message)
                    STRONG_WARNING, WARNING -> logger.error(message)
                    INFO, OUTPUT -> logger.info(message)
                    LOGGING -> logger.debug(message)
                }
            }
            when {
                messages.hasErrors() || result == null ->
                    error("Compilation of Collektive program $name failed:\n$finalCode")
                else -> loadMethod()
            }
        }.getOrThrow()
        return RunCollektiveProgram(node, methodToCall, name)
    }

    override fun createCondition(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>?,
        node: Node<Any?>?,
        time: TimeDistribution<Any?>,
        actionable: Actionable<Any?>,
        additionalParameters: Any?,
    ): Condition<Any?> = object : AbstractCondition<Any>(requireNotNull(node)) {
        override fun getContext() = Context.LOCAL

        override fun getPropensityContribution(): Double = 1.0

        override fun isValid(): Boolean = true
    }

    override fun createReaction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        node: Node<Any?>,
        timeDistribution: TimeDistribution<Any?>,
        parameter: Any?,
    ): Reaction<Any?> = Event(node, timeDistribution).also {
        it.actions = ListSet.of(
            createAction(randomGenerator, environment, node, timeDistribution, it, parameter),
        )
    }

    override fun createTimeDistribution(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        node: Node<Any?>?,
        parameter: Any?,
    ): TimeDistribution<Any?> {
        val frequency = when (parameter) {
            null -> 1.0
            is Number -> parameter.toDouble()
            is String -> parameter.toDouble()
            else -> error("Invalid time distribution parameter: $parameter")
        }
        return DiracComb(DoubleTime(randomGenerator.nextDouble(0.0, 1.0 / frequency)), frequency)
    }

    override fun createNode(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        parameter: Any?,
    ): Node<Any?> = GenericNode(environment).also { genericNode ->
        genericNode.addProperty(
            CollektiveDevice(
                environment,
                genericNode,
                when (parameter) {
                    null -> null
                    is Number -> DoubleTime(parameter.toDouble())
                    is String -> DoubleTime(parameter.toDouble())
                    else -> error("Invalid message retention time: $parameter")
                },
            ),
        )
    }

    companion object {

        private object ScriptEngine {
            operator fun getValue(thisRef: Any?, property: KProperty<*>) =
                ScriptEngineManager().getEngineByName(property.name)
                    ?: error("No script engine with ${property.name} found.")
        }

        private val findPackage = Regex("""package\s+((\w+\.)*\w+)(\s|;|${'$'}|/)""", RegexOption.MULTILINE)
        private val validName = Regex("^[a-zA-Z_][a-zA-Z0-9_]*$", RegexOption.MULTILINE)
        private val logger = LoggerFactory.getLogger(CollektiveIncarnation::class.java)
        private val kotlin by ScriptEngine
        private val defaultLambda: (Any?) -> Any? = { Double.NaN }

        private val propertyCache: LoadingCache<String, (Any?) -> Any?> = Caffeine
            .newBuilder()
            .maximumSize(1000)
            .build { property ->
                runCatching {
                    @Suppress("UNCHECKED_CAST")
                    when (val interpreted = kotlin.eval(property)) {
                        is (Nothing) -> Any? -> interpreted
                        else -> defaultLambda
                    } as (Any?) -> Any?
                }.getOrElse { defaultLambda }
            }

        private val classLoaders: LoadingCache<String, URLClassLoader> =
            Caffeine.newBuilder().maximumSize(1000).build { name ->
                val outputFolder = createTempDirectory(name).toFile()
                URLClassLoader(arrayOf(outputFolder.toURI().toURL()), Thread.currentThread().contextClassLoader)
            }

        /**
         * Convert the source set to a list of files.
         */
        private fun Any?.toFiles(): List<File> = when (this) {
            null -> emptyList()
            is File -> listOf(this)
            is CharSequence -> {
                val file = File(toString())
                check(file.exists()) {
                    "Collektive source root ${file.absolutePath} does not exist"
                }
                listOf(file)
            }

            is Iterable<*> -> flatMap { files -> files.toFiles() }
            else -> error("Invalid source setof type ${this::class.simpleName ?: "anonymous"}: $this")
        }
    }
}
