package it.unibo.alchemist.actions

import it.unibo.alchemist.model.Action
import it.unibo.alchemist.model.Actionable
import it.unibo.alchemist.model.Context
import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Reaction
import it.unibo.alchemist.model.TimeDistribution
import it.unibo.alchemist.model.actions.AbstractAction
import java.util.random.RandomGenerator

/**
 * TODO.
 */
class RunCollektiveProgram<P : Position<P>>(
//    randomGenerator: RandomGenerator,
//    environment: Environment<Any?, P>,
    node: Node<Any?>?,
//    time: TimeDistribution<Any?>,
//    actionable: Actionable<Any?>,
//    additionalParameters: String,
): AbstractAction<Any?>(
    requireNotNull(node) { "Collektive does not support an environment with null as nodes" },
    ) {
    override fun cloneAction(node: Node<Any?>?, reaction: Reaction<Any?>?): Action<Any?> {
        TODO("Not yet implemented")
    }

    override fun execute() {
        TODO("Not yet implemented")
    }

    override fun getContext(): Context {
        TODO("Not yet implemented")
    }
}
