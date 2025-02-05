package it.unibo.alchemist.collektive.device

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.alchemist.model.molecules.SimpleMolecule
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.neighboring
import it.unibo.collektive.alchemist.device.sensors.EnvironmentVariables
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundSendOperation
import it.unibo.collektive.path.Path

/**
 * Representation of a Collektive device in Alchemist.
 * [P] is the position type, the [environment] property represent the environment in which the device is located,
 * the [node] property represent a node in the environment, [retainMessagesFor] is the time for which messages
 * are retained.
 */
class CollektiveDevice<P>(
    private val environment: Environment<Any?, P>,
    override val node: Node<Any?>,
    private val retainMessagesFor: Time? = null,
) : NodeProperty<Any?>,
    Network<Int>,
    EnvironmentVariables,
    DistanceSensor where P : Position<P> {

    private val neighborsCount: Int get() = environment.getNeighborhood(node).size()
    private var isNewRound = true
    override val requiresSerialization: Boolean
        get() = false
    override val neighbors: Set<Int> get() = environment.getNeighborhood(node).map { it.id }.toSet()

    /**
     * The ID of the node.
     */
    val id = node.id

    /**
     * The current time.
     */
    var currentTime: Time = Time.ZERO

    private val validMessages: MutableMap<Time, Message<Int>> = mutableMapOf()

    private fun receiveMessage(
        time: Time,
        message: Message<Int>,
    ) {
        validMessages += time to message
    }

    override fun <ID : Any> Aggregate<ID>.distances(): Field<ID, Double> =
        environment.getPosition(node).let { nodePosition ->
            neighboring(nodePosition.coordinates).map { coordinates ->
                // TODO: make a makePosition(DoubleArray) in the simulator
                nodePosition.distanceTo(environment.makePosition(coordinates.toList()))
            }
        }

    override fun cloneOnNewNode(node: Node<Any?>): NodeProperty<Any?> =
        CollektiveDevice(environment, node, retainMessagesFor)

    override fun <T> messageAt(path: Path): Map<Int, T> = when {
        validMessages.isEmpty() -> emptyMap()
        else -> {
            if (isNewRound && retainMessagesFor != null) {
                validMessages.entries.removeIf { (receivedAt, _) -> receivedAt + retainMessagesFor < currentTime }
                isNewRound = false
            }
            val result = LinkedHashMap<Int, T>(neighborsCount, 1f)
            for ((_, message) in validMessages) {
                @Suppress("UNCHECKED_CAST")
                result[message.senderId] = message.sharedData[path] as T
            }
            result
        }
    }

    override fun deliver(message: OutboundSendOperation<Int>) {
        if (message.isNotEmpty()) {
            val neighboringNodes = environment.getNeighborhood(node)
            if (!neighboringNodes.isEmpty) {
                val neighborhood =
                    neighboringNodes.mapNotNull { node ->
                        @Suppress("UNCHECKED_CAST")
                        node.properties.firstOrNull { it is CollektiveDevice<*> } as? CollektiveDevice<P>
                    }
                neighborhood.forEach { neighbor ->
                    neighbor.receiveMessage(
                        currentTime,
                        message.messagesFor(neighbor.id),
                    )
                }
            }
        }
        isNewRound = true
        if (retainMessagesFor == null) {
            validMessages.clear()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(name: String): T = node.getConcentration(SimpleMolecule(name)) as T

    override fun <T> getOrNull(name: String): T? =
        if (isDefined(name)) {
            get(name)
        } else {
            null
        }

    override fun <T> getOrDefault(
        name: String,
        default: T,
    ): T = getOrNull(name) ?: default

    override fun isDefined(name: String): Boolean = node.contains(SimpleMolecule(name))

    override fun <T> set(
        name: String,
        value: T,
    ): T = value.also { node.setConcentration(SimpleMolecule(name), it) }
}
