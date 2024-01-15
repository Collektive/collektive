package it.unibo.alchemist

import it.unibo.alchemist.model.Action
import it.unibo.alchemist.model.Actionable
import it.unibo.alchemist.model.Condition
import it.unibo.alchemist.model.Context
import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Incarnation
import it.unibo.alchemist.model.Molecule
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Node.Companion.asProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Reaction
import it.unibo.alchemist.model.TimeDistribution
import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.conditions.AbstractCondition
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.alchemist.model.nodes.GenericNode
import it.unibo.alchemist.model.reactions.Event
import it.unibo.alchemist.model.timedistributions.DiracComb
import it.unibo.alchemist.model.times.DoubleTime
import it.unibo.alchemist.util.RandomGenerators.nextDouble
import it.unibo.collektive.Collektive
import it.unibo.collektive.alchemist.device.CollektiveDevice
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet
import kotlin.reflect.jvm.kotlinFunction

/**
 * Collektive incarnation in Alchemist.
 */
class CollektiveIncarnation<P> : Incarnation<Any?, P> where P : Position<P> {
    override fun getProperty(node: Node<Any?>, molecule: Molecule, property: String?): Double =
        when (val data = node.getConcentration(molecule)) {
            is Double -> data
            is Number -> data.toDouble()
            is String -> data.toDoubleOrNull() ?: Double.NaN
            else -> error("$data is not a doublify-able value")
        }

    override fun createMolecule(s: String) = SimpleMolecule(s)

    override fun createConcentration(s: String?) = s

    override fun createConcentration() = TODO("create concentration not yet implemented")

    override fun createAction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        node: Node<Any?>?,
        time: TimeDistribution<Any?>,
        actionable: Actionable<Any?>,
        additionalParameters: String,
    ): Action<Any?> = object : AbstractAction<Any?>(
        requireNotNull(node) { "Collektive does not support an environment with null as nodes" },
    ) {
        val programIdentifier = SimpleMolecule(additionalParameters)

        val localDevice: CollektiveDevice<P> = node?.asProperty() ?: error("Trying to create action for null node")
        val run: () -> Any?

        val className = additionalParameters.substringBeforeLast(".")
        val methodName = additionalParameters.substringAfterLast(".")
        val classNameFoo = Class.forName(className)
        val method = classNameFoo.methods.find { it.name == methodName }
            ?: error("Method $additionalParameters not found")

        init {
            declareDependencyTo(programIdentifier)
            val collektive = Collektive(localDevice.id, localDevice) {
                method.kotlinFunction?.call(localDevice, this@Collektive)
                    ?: error("No aggregate function found")
            }
            run = { collektive.cycle() }
        }

        override fun cloneAction(node: Node<Any?>?, reaction: Reaction<Any?>?): Action<Any?> {
            TODO("Not yet implemented")
        }

        override fun execute() {
            localDevice.currentTime = time.nextOccurence
            run().also {
                node?.setConcentration(
                    SimpleMolecule(method.name),
                    it,
                ) ?: error("Trying to set concentration for null node")
            }
        }

        override fun getContext(): Context = Context.NEIGHBORHOOD // or Context.LOCAL
    }

    override fun createCondition(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>?,
        node: Node<Any?>?,
        time: TimeDistribution<Any?>,
        actionable: Actionable<Any?>,
        additionalParameters: String?,
    ): Condition<Any?> = object : AbstractCondition<Any>(requireNotNull(node)) {
        override fun getContext() = Context.LOCAL
        override fun getPropensityContribution(): Double = 1.0
        override fun isValid(): Boolean = true
    }

    override fun createReaction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        node: Node<Any?>?,
        timeDistribution: TimeDistribution<Any?>,
        parameter: String,
    ): Reaction<Any?> = Event(node, timeDistribution).also {

        it.actions = ListSet.of(
            createAction(randomGenerator, environment, node, timeDistribution, it, parameter),
        )
    }

    override fun createTimeDistribution(
        randomGenerator: RandomGenerator?,
        environment: Environment<Any?, P>,
        node: Node<Any?>?,
        parameter: String?,
    ): TimeDistribution<Any?> = parameter.toDefaultDouble().let { frequency ->
        DiracComb(randomGenerator?.let { DoubleTime(it.nextDouble(0.0, 1.0 / frequency)) }, frequency)
    }

    override fun createNode(
        randomGenerator: RandomGenerator?,
        environment: Environment<Any?, P>,
        parameter: String?,
    ): Node<Any?> = GenericNode(environment).also {
        it.addProperty(CollektiveDevice(environment, it,  DoubleTime(parameter.toDefaultDouble())))
    }

    private fun String?.toDefaultDouble(): Double = this?.toDoubleOrNull() ?: 1.0
}
