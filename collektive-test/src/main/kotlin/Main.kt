import it.unibo.alchemist.boundary.swingui.impl.SingleRunGUI
import it.unibo.alchemist.core.implementations.Engine
import it.unibo.alchemist.model.CollektiveIncarnation
import it.unibo.alchemist.model.implementations.environments.Continuous2DEnvironment
import it.unibo.alchemist.model.implementations.linkingrules.ConnectWithinDistance
import it.unibo.alchemist.model.implementations.positions.Euclidean2DPosition
import it.unibo.alchemist.model.interfaces.*
import org.apache.commons.math3.random.RandomGeneratorFactory
import java.util.*
import javax.swing.JFrame

fun main() {
    val incarnation = CollektiveIncarnation<Euclidean2DPosition>()
    val environment = Continuous2DEnvironment(incarnation)
    val linkingRule: LinkingRule<Any, Euclidean2DPosition> =
        ConnectWithinDistance(2.0)
    environment.linkingRule = linkingRule
    // Creates nodes
    for (i in 0..20) {
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
                "Aggregate.entrypoint"
            )
        )
        environment.addNode(
            node,
            Euclidean2DPosition(
                Random().nextDouble(),
                Random().nextDouble()
            )
        )
    }
    val engine = Engine(environment,200)
    environment.simulation = engine
    // Start GUI
    SingleRunGUI.make(engine, JFrame.EXIT_ON_CLOSE)
    engine.run()
}
