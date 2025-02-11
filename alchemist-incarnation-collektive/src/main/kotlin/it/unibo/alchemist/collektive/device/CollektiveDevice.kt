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
import it.unibo.collektive.networking.Mailbox
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.NoNeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.path.Path
import kotlinx.serialization.KSerializer

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
    Mailbox<Int>,
    EnvironmentVariables,
    DistanceSensor where P : Position<P> {
    private data class TimedMessage(
        val receivedAt: Time,
        val payload: Message<Int, *>,
    )

    /**
     * The current time.
     */
    var currentTime: Time = Time.ZERO

    /**
     * The ID of the node.
     */
    val id = node.id

    private val validMessages: MutableList<TimedMessage> = mutableListOf()

    private fun receiveMessage(
        time: Time,
        message: Message<Int, *>,
    ) {
        validMessages += TimedMessage(time, message)
    }

    override fun <ID : Any> Aggregate<ID>.distances(): Field<ID, Double> =
        environment.getPosition(node).let { nodePosition ->
            neighboring(nodePosition.coordinates).map { position ->
                // TODO: call makePosition(DoubleArray) in the simulator as the new version arrives
                nodePosition.distanceTo(environment.makePosition(position.toList()))
            }
        }

    override fun cloneOnNewNode(node: Node<Any?>): NodeProperty<Any?> =
        CollektiveDevice(environment, node, retainMessagesFor)

    override fun deliverableFor(
        id: Int,
        outboundMessage: OutboundEnvelope<Int>,
    ) {
        if (outboundMessage.isNotEmpty()) {
            val neighborsNodes = environment.getNeighborhood(node)
            if (!neighborsNodes.isEmpty) {
                val neighborhood =
                    neighborsNodes.mapNotNull { node ->
                        @Suppress("UNCHECKED_CAST")
                        node.properties.firstOrNull { it is CollektiveDevice<*> } as? CollektiveDevice<P>
                    }
                neighborhood.forEach { neighbor ->
                    neighbor.deliverableReceived(outboundMessage.prepareMessageFor(node.id))
                }
            }
        }
    }

    override fun deliverableReceived(message: Message<Int, *>) {
        receiveMessage(currentTime, message)
    }

    override fun currentInbound(): NeighborsData<Int> {
        if (validMessages.isEmpty()) {
            return NoNeighborsData()
        }
        val messages: List<Message<Int, *>> =
            when {
                retainMessagesFor == null -> validMessages.map { it.payload }.also { validMessages.clear() }
                else -> {
                    validMessages.retainAll { it.receivedAt + retainMessagesFor >= currentTime }
                    validMessages.map { it.payload }
                }
            }
        return object : NeighborsData<Int> {
            override val neighbors: Set<Int> by lazy { messages.map { it.senderId }.toSet() }

            @Suppress("UNCHECKED_CAST")
            override fun <Value> dataAt(
                path: Path,
                kClass: KSerializer<Value>,
            ): Map<Int, Value> =
                messages
                    .associateBy { it.senderId }
                    .mapValues { (_, message) ->
                        message.sharedData.getOrElse(path) { NoValue } as Value
                    }.filter { it.value != NoValue }
        }
    }

    private object NoValue

    @Suppress("UNCHECKED_CAST")
    override fun <T> get(name: String): T = node.getConcentration(SimpleMolecule(name)) as T

    override fun <T> getOrNull(name: String): T? =
        when {
            isDefined(name) -> get(name)
            else -> null
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
