package it.unibo.alchemist.model

import it.unibo.alchemist.boundary.swingui.impl.SingleRunGUI
import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.model.api.SupportedIncarnations
import it.unibo.alchemist.model.implementations.environments.Continuous2DEnvironment
import it.unibo.alchemist.model.implementations.linkingrules.ConnectWithinDistance
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.interfaces.*
import it.unibo.alchemist.testsupport.createEmptyEnvironment
import it.unibo.alchemist.testsupport.runInCurrentThread
import it.unibo.alchemist.testsupport.startSimulation
import org.apache.commons.math3.random.RandomGeneratorFactory
import java.util.*
import javax.swing.JFrame

fun main(args: Array<String>) {
    val incarnation = CollektiveIncarnation<Euclidean2DPosition>()
    val environment = Continuous2DEnvironment(incarnation)
    val linkingRule: LinkingRule<Any, Euclidean2DPosition> =
        ConnectWithinDistance(9.0)
    environment.linkingRule = linkingRule
    for (i in 0..2) {
        val randomGenerator = RandomGeneratorFactory.createRandomGenerator(Random(1))
        val node = incarnation.createNode(
            randomGenerator,
            environment,
            null
        )
        node.addReaction(
            incarnation.createReaction(
                randomGenerator,
                environment,
                node,
                incarnation.createTimeDistribution(randomGenerator, environment, node, null),
                "Main.entrypoint"
            )
        )
        environment.addNode(
            node,
            Euclidean2DPosition(0.0, 0.0)
        )
    }
    val engine = Engine(environment,10)
    environment.simulation = engine

    //engine.play()

    SingleRunGUI.make(engine, JFrame.EXIT_ON_CLOSE)
    engine.run()
}
