package it.unibo.collektive.alchemist.incarnation

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
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.alchemist.device.CollektiveDevice
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet
import java.lang.reflect.Method

/**
 * Collektive incarnation in Alchemist.
 */
class CollektiveIncarnation<P> : Incarnation<Any, P> where P : Position<P> {
    override fun getProperty(node: Node<Any>, molecule: Molecule, property: String?): Double =
        when (val data = node.getConcentration(molecule)) {
            is Double -> data
            is Number -> data.toDouble()
            is String -> data.toDoubleOrNull() ?: Double.NaN
            else -> error("$data is not a doublify-able value")
        }

    override fun createMolecule(s: String) = SimpleMolecule(s)

    override fun createConcentration(s: String?) = s

    override fun createConcentration(): Any = Any()

    override fun createAction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any, P>,
        node: Node<Any>?,
        time: TimeDistribution<Any>,
        actionable: Actionable<Any>,
        additionalParameters: String,
    ): Action<Any> = object : AbstractAction<Any>(
        requireNotNull(node) {
            "Global Collektive programs not supported yet"
        },
    ) {
        val aggregateEntrypoint: Method
        val programIdentifier = SimpleMolecule(additionalParameters)
        val localDevice: CollektiveDevice<P> by lazy { this.node.asProperty() }
        val run: () -> AggregateResult<*>

        init {
            declareDependencyTo(programIdentifier)
            val lastDotIndex = additionalParameters.lastIndexOf('.')
            val clazz = Class.forName(additionalParameters.substring(0 until lastDotIndex))
            val instance = clazz.constructors.first().newInstance(
                requireNotNull(node).asProperty<Any, CollektiveDevice<P>>(),
            )
            aggregateEntrypoint = clazz
                .methods
                .asSequence()
                .filter { it.returnType == AggregateResult::class.java }
                .first {
                    it.name == additionalParameters.substring(lastDotIndex + 1) && it.parameters.isEmpty()
                }
            run = { aggregateEntrypoint.invoke(instance) as AggregateResult<*> }
        }

        override fun cloneAction(node: Node<Any>, reaction: Reaction<Any>): Action<Any> {
            TODO("Not yet implemented")
        }

        override fun execute() {
            localDevice.currentTime = time.nextOccurence
            val result = run()
            node?.setConcentration(
                programIdentifier,
                result.result ?: error("Trying to set null as concentration for $node"),
            )
            localDevice.write(result.toSend)
        }

        override fun getContext(): Context = Context.NEIGHBORHOOD
    }

    override fun createCondition(
        randomGenerator: RandomGenerator,
        environment: Environment<Any, P>,
        node: Node<Any>?,
        time: TimeDistribution<Any>?,
        actionable: Actionable<Any>?,
        additionalParameters: String?,
    ): Condition<Any> = object : AbstractCondition<Any>(requireNotNull(node)) {
        override fun getContext() = Context.LOCAL
        override fun getPropensityContribution(): Double = 1.0
        override fun isValid(): Boolean = true
    }

    override fun createReaction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any, P>,
        node: Node<Any>?,
        timeDistribution: TimeDistribution<Any>,
        parameter: String,
    ): Reaction<Any> = Event(node, timeDistribution).also {
        it.actions = ListSet.of(
            createAction(randomGenerator, environment, node, timeDistribution, it, parameter),
        )
    }

    override fun createTimeDistribution(
        randomGenerator: RandomGenerator,
        environment: Environment<Any, P>,
        node: Node<Any>?,
        parameter: String?,
    ): TimeDistribution<Any> = parameter.toDefaultDouble().let { frequency ->
        DiracComb(DoubleTime(randomGenerator.nextDouble(0.0, 1.0 / frequency)), frequency)
    }

    override fun createNode(
        randomGenerator: RandomGenerator,
        environment: Environment<Any, P>,
        parameter: String?,
    ): Node<Any> = GenericNode(environment).also {
        it.addProperty(CollektiveDevice(environment, it, DoubleTime(parameter.toDefaultDouble())))
    }

    private fun String?.toDefaultDouble(): Double = this?.toDoubleOrNull() ?: 1.0
}
