package it.unibo.alchemist.actions

import it.unibo.alchemist.collektive.device.CollektiveDevice
import it.unibo.alchemist.model.Action
import it.unibo.alchemist.model.Context
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Node.Companion.asProperty
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Reaction
import it.unibo.alchemist.model.actions.AbstractAction
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.path.FullPathFactory
import java.lang.reflect.Method
import java.lang.reflect.Parameter
import kotlin.reflect.jvm.kotlinFunction

/**
 * An Alchemist [Action] that runs a [Collektive] program.
 * Requires a [node], a program [name], and the actual [program] to execute.
 */
class RunCollektiveProgram<P : Position<P>>(
    node: Node<Any?>,
    val name: String,
    val program: Aggregate<Int>.(CollektiveDevice<P>) -> Any?,
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
        collektiveProgram =
            Collektive(localDevice.id, network = localDevice, pathFactory = FullPathFactory) {
                program(localDevice)
            }
    }

    /**
     * Create a [RunCollektiveProgram] with a specific [entrypoint] and a [node].
     */
    constructor(
        node: Node<Any?>,
        entrypoint: String,
    ) : this(node, entrypoint, findEntrypoint(entrypoint))

    /**
     * Create a [RunCollektiveProgram] with a specific [entrypoint] and a [node].
     */
    @JvmOverloads
    constructor(
        node: Node<Any?>,
        entrypoint: Method,
        name: String = entrypoint.name,
    ) : this(node, name, buildEntryPoint(entrypoint))

    override fun cloneAction(node: Node<Any?>, reaction: Reaction<Any?>): Action<Any?> =
        RunCollektiveProgram(node, name)

    override fun execute() {
        collektiveProgram.cycle().also {
            node.setConcentration(programIdentifier, it)
        }
    }

    override fun getContext(): Context = Context.NEIGHBORHOOD

    private companion object {
        private fun <P : Position<P>> findEntrypoint(
            entrypoint: String,
        ): Aggregate<Int>.(CollektiveDevice<P>) -> Any? {
            val className = entrypoint.substringBeforeLast(".")
            val methodName = entrypoint.substringAfterLast(".")
            val clazz = Class.forName(className)
            val method =
                clazz.methods.find { it.name == methodName }
                    ?: error("Entrypoint $entrypoint not found, no method $methodName found in class $className")
            return buildEntryPoint(method)
        }

        private fun <P : Position<P>> buildEntryPoint(method: Method): Aggregate<Int>.(CollektiveDevice<P>) -> Any? {
            val ktFunction =
                checkNotNull(method.kotlinFunction) {
                    "Method ${method.name} in class ${method.declaringClass.name}" +
                        " cannot be converted to a Kotlin function"
                }
            // Build the lambda function to be executed
            return { device: CollektiveDevice<P> ->
                val parameters =
                    method.parameters
                        .map {
                            when {
                                it.type.isAssignableFrom(Aggregate::class.java) -> this
                                it.type.isAssignableFrom(CollektiveDevice::class.java) -> device
                                it.type.isAssignableFrom(Node::class.java) -> device.node
                                device.node.hasPropertyCompatibleWith(it) -> device.node.getPropertyCompatibleWith(it)
                                else -> error("Unsupported type ${it.type} in entrypoint ${ktFunction.name}")
                            }
                        }.toTypedArray()
                ktFunction.call(*parameters)
            }
        }

        private fun Node<*>.hasPropertyCompatibleWith(parameter: Parameter): Boolean =
            properties.any { parameter.type.isAssignableFrom(it::class.java) }

        private fun Node<*>.getPropertyCompatibleWith(property: Parameter): NodeProperty<*> =
            properties.first { property.type.isAssignableFrom(it::class.java) }
    }
}
