package it.unibo.collektive.examples.main

import it.unibo.alchemist.core.Engine
import it.unibo.alchemist.model.LinkingRule
import it.unibo.alchemist.model.environments.Continuous2DEnvironment
import it.unibo.alchemist.model.linkingrules.ConnectWithinDistance
import it.unibo.alchemist.model.positions.Euclidean2DPosition
import it.unibo.collektive.alchemist.incarnation.CollektiveIncarnation
import org.apache.commons.math3.random.RandomGeneratorFactory
import java.util.*
import javax.swing.JFrame

/**
 * The quantity of [NODES] to set up in the simulation.
 */
const val NODES = 200

/**
 * The [MAX_SPACE].
 */
const val MAX_SPACE = 5.0

/**
 * TODO.
 */
fun main() {
    val incarnation = CollektiveIncarnation<Euclidean2DPosition>()
    val environment = Continuous2DEnvironment(incarnation)
    val linkingRule: LinkingRule<Any?, Euclidean2DPosition> = ConnectWithinDistance(1.0)
    environment.linkingRule = linkingRule

    // Creation range
    val minDouble = 0.0
    val maxDouble = MAX_SPACE
    val range = maxDouble - minDouble

    // Creates nodes
    repeat(NODES) {
        val randomGenerator = RandomGeneratorFactory.createRandomGenerator(Random(1))
        val node =
            incarnation.createNode(randomGenerator, environment, null).also { n ->
                n.addReaction(
                    incarnation.createReaction(
                        randomGenerator,
                        environment,
                        n,
                        incarnation.createTimeDistribution(randomGenerator, environment, n, null),
                        "it.unibo.collektive.examples.aggregate.AggregateFunctionsKt.gradient",
                    ),
                )
            }
        environment.addNode(
            node,
            Euclidean2DPosition(
                Random().nextDouble() * range + minDouble,
                Random().nextDouble() * range + minDouble,
            ),
        )
    }

    val engine = Engine(environment)
    environment.simulation = engine

    // Start GUI
    @Suppress("DEPRECATION")
    it.unibo.alchemist.boundary.swingui.impl.SingleRunGUI.make(
        engine,
        "/Users/angela/DocsUni/UNI/TESI/collektive/collektive-examples/src/main/resources/effects/gradient.json",
        JFrame.EXIT_ON_CLOSE,
    )
    engine.run()
}
