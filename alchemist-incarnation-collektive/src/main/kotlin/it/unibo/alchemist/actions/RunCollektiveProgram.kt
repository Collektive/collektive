package it.unibo.alchemist.actions

import it.unibo.alchemist.model.Action
import it.unibo.alchemist.model.Context
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Node.Companion.asProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Reaction
import it.unibo.alchemist.model.TimeDistribution
import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.alchemist.device.CollektiveDevice
import kotlin.reflect.jvm.kotlinFunction

/**
 * An Alchemist [Action] that runs a [Collektive] program.
 * It takes the [node] on which execute the action, the [time] distribution and the
 * aggregate function to execute.
 */
class RunCollektiveProgram<P : Position<P>>(
    private val node: Node<Any?>?,
    private val time: TimeDistribution<Any?>,
    additionalParameters: String,
) : AbstractAction<Any?>(
    requireNotNull(node) { "Collektive does not support an environment with null as nodes" },
) {

    private object AggregatePlaceHolder

    private val programIdentifier = SimpleMolecule(additionalParameters)
    private val localDevice: CollektiveDevice<P> = node?.asProperty() ?: error("Trying to create action for null node")
    private val run: () -> Any?
    private val className = additionalParameters.substringBeforeLast(".")
    private val methodName = additionalParameters.substringAfterLast(".")
    private val classNameFoo = Class.forName(className)
    private val method = classNameFoo.methods.find { it.name == methodName }
        ?: error("Method $additionalParameters not found")

    init {
        declareDependencyTo(programIdentifier)
        val parameters = method.parameters.map { param ->
            when {
                param.type.isAssignableFrom(Aggregate::class.java) -> AggregatePlaceHolder
                param.type.isAssignableFrom(CollektiveDevice::class.java) -> localDevice
                else -> error("Unsupported parameter of type ${param.type}")
            }
        }
        val function = method.kotlinFunction ?: error("No aggregate function found for $programIdentifier")
        val collektive = Collektive(localDevice.id, localDevice) {
            function.call(*parameters.map { if (it == AggregatePlaceHolder) this else it }.toTypedArray())
        }
        run = { collektive.cycle() }
    }

    override fun cloneAction(node: Node<Any?>?, reaction: Reaction<Any?>?): Action<Any?> {
        TODO("Not yet implemented")
    }

    override fun execute() {
        localDevice.currentTime = time.nextOccurence
        run.invoke().also {
            node?.setConcentration(
                SimpleMolecule(method.name),
                it,
            ) ?: error("Trying to set concentration for null node")
        }
    }

    override fun getContext(): Context = Context.NEIGHBORHOOD
}
