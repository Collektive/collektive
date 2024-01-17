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
 * TODO.
 */
class RunCollektiveProgram<P : Position<P>>(
    private val node: Node<Any?>?,
    private val time: TimeDistribution<Any?>,
    additionalParameters: String,
) : AbstractAction<Any?>(
    requireNotNull(node) { "Collektive does not support an environment with null as nodes" },
) {
    private val programIdentifier = SimpleMolecule(additionalParameters)

    private val localDevice: CollektiveDevice<P> = node?.asProperty() ?: error("Trying to create action for null node")
    private val run: () -> Any?

    private val className = additionalParameters.substringBeforeLast(".")
    private val methodName = additionalParameters.substringAfterLast(".")
    private val classNameFoo = Class.forName(className)
    private val method = classNameFoo.methods.find { it.name == methodName }
        ?: error("Method $additionalParameters not found")

    private var parameterCache: Map<Class<*>, Any> = method.parameters.associate { param ->
        when {
            param.type.isAssignableFrom(Aggregate::class.java) -> param.type to Aggregate::class.java
            param.type.isAssignableFrom(CollektiveDevice::class.java) -> param.type to localDevice
            else -> error("No allowed context parameters found, expected at least Aggregate as context")
        }
    }

    init {
        declareDependencyTo(programIdentifier)
        val collektive = Collektive(localDevice.id, localDevice) {
            parameterCache += mapOf(Aggregate::class.java to this)
            method.kotlinFunction?.call(*parameterCache.values.toTypedArray()) ?: error("No aggregate function found")
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
