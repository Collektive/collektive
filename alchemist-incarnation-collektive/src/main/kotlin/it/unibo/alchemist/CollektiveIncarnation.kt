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
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet
import javax.script.ScriptEngineManager
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
                val toInvoke = cache.get("import kotlin.math.*; val x: ($concentrationType) -> Any? = { $property }; x")
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
    ): Action<Any?> = RunCollektiveProgram(
        requireNotNull(node) { "Collektive requires a device and cannot execute in a Global Reaction" },
        additionalParameters?.toString().orEmpty(),
    )

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
        private val kotlin by ScriptEngine
        private val defaultLambda: (Any?) -> Any? = { Double.NaN }

        private val cache: LoadingCache<String, (Any?) -> Any?> = Caffeine.newBuilder()
            .build { property ->
                runCatching {
                    @Suppress("UNCHECKED_CAST")
                    when (val interpreted = kotlin.eval(property)) {
                        is (Nothing) -> Any? -> interpreted
                        else -> defaultLambda
                    } as (Any?) -> Any?
                }.getOrElse { defaultLambda }
            }
    }
}
