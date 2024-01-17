package it.unibo.alchemist

import it.unibo.alchemist.actions.RunCollektiveProgram
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
import it.unibo.collektive.alchemist.device.CollektiveDevice
import org.apache.commons.math3.random.RandomGenerator
import org.danilopianini.util.ListSet

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

    override fun createConcentration() = 0.0

    override fun createAction(
        randomGenerator: RandomGenerator,
        environment: Environment<Any?, P>,
        node: Node<Any?>?,
        time: TimeDistribution<Any?>,
        actionable: Actionable<Any?>,
        additionalParameters: String,
    ): Action<Any?> =
        RunCollektiveProgram(node, time, additionalParameters)

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
        it.addProperty(CollektiveDevice(environment, it, DoubleTime(parameter.toDefaultDouble())))
    }

    private fun String?.toDefaultDouble(): Double = this?.toDoubleOrNull() ?: 1.0
}
