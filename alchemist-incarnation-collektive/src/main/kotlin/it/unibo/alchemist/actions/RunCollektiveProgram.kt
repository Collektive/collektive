package it.unibo.alchemist.actions

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.Action
import it.unibo.alchemist.model.Context
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Node.Companion.asProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Reaction
import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import java.lang.reflect.Method
import kotlin.reflect.jvm.kotlinFunction

/**
 * An Alchemist [Action] that runs a [Collektive] program.
 * Requires a [node], a program [name], and the actual [program] to execute.
 */
class RunCollektiveProgram<P : Position<P>>(
    node: Node<Any?>,
    val name: String,
    val program: context(CollektiveDevice<P>) Aggregate<Int>.() -> Any?,
) : AbstractAction<Any?>(node) {

    private val programIdentifier = SimpleMolecule(name)

    /**
     * The [CollektiveDevice] associated with the [node].
     */
    val localDevice: CollektiveDevice<P> = node.asProperty()

    /**
     * The [Collektive] program on which cycles will be executed.
     */
    val collektiveProgram: Collektive<Int, Any?>

    init {
        declareDependencyTo(programIdentifier)
        collektiveProgram = Collektive(localDevice.id, network = localDevice) {
            program(localDevice, this)
        }
    }

    /**
     * Create a [RunCollektiveProgram] with a specific [entrypoint] and a [node].
     */
    constructor(
        node: Node<Any?>,
        entrypoint: String,
    ) : this(node, entrypoint, findEntrypoint(entrypoint, node.asProperty()))

    /**
     * Create a [RunCollektiveProgram] with a specific [entrypoint] and a [node].
     */
    @JvmOverloads
    constructor(
        node: Node<Any?>,
        entrypoint: Method,
        name: String = entrypoint.name,
    ) : this(node, name, buildEntryPoint(entrypoint, node.asProperty()))

    override fun cloneAction(node: Node<Any?>, reaction: Reaction<Any?>): Action<Any?> =
        RunCollektiveProgram(node, name, program)

    override fun execute() {
        collektiveProgram.cycle().also {
            node.setConcentration(programIdentifier, it)
        }
    }

    override fun getContext(): Context = Context.NEIGHBORHOOD

    companion object {

        private fun <P : Position<P>> findEntrypoint(
            entrypoint: String,
            localDevice: CollektiveDevice<P>,
        ): context(CollektiveDevice<P>) Aggregate<Int>.() -> Any? {
            val className = entrypoint.substringBeforeLast(".")
            val methodName = entrypoint.substringAfterLast(".")
            val clazz = Class.forName(className)
            val method = clazz.methods.find { it.name == methodName }
                ?: error("Entrypoint $entrypoint not found, no method $methodName found in class $className")
            return buildEntryPoint(method, localDevice)
        }

        private fun <P : Position<P>> buildEntryPoint(
            method: Method,
            localDevice: CollektiveDevice<P>,
        ): context(CollektiveDevice<P>) Aggregate<Int>.() -> Any? {
            val ktfunction = checkNotNull(method.kotlinFunction) {
                "Method ${method.name} in class ${method.declaringClass.name} cannot be converted to a Kotlin function"
            }
            return {
                val parameters = method.parameters.map {
                    when {
                        it.type.isAssignableFrom(Aggregate::class.java) -> this
                        it.type.isAssignableFrom(CollektiveDevice::class.java) -> localDevice
                        it.type.isAssignableFrom(Node::class.java) -> localDevice.node
                        else -> error("Unsupported type ${it.type} in entrypoint ${ktfunction.name}")
                    }
                }.toTypedArray()
                ktfunction.call(*parameters)
            }
        }
    }
}
