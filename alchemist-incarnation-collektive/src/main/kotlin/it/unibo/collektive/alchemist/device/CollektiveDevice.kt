package it.unibo.collektive.alchemist.device

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.collektive.ID
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.alchemist.utils.toId
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage
import kotlin.math.abs

/**
 * Representation of a Collektive device in Alchemist.
 * [P] is the position type, the [environment] property represent the environment in which the device is located,
 * the [node] property represent a node in the environment, [retainMessagesFor] is the time for which messages
 * are retained.
 */
class CollektiveDevice<P>(
    private val environment: Environment<Any?, P>,
    override val node: Node<Any?>,
    private val retainMessagesFor: Time,
) : NodeProperty<Any?>, Network, DistanceSensor where P : Position<P> {
    private data class TimedMessage(val receivedAt: Time, val payload: InboundMessage)

    /**
     * The current time.
     */
    var currentTime: Time = Time.ZERO

    /**
     * The ID of the node.
     */
    val id: ID = IntId(node.id)

    private var validMessages: Iterable<TimedMessage> = emptySet()

    private fun receiveMessage(
        time: Time,
        message: InboundMessage,
    ) {
        validMessages += TimedMessage(time, message)
    }

    override fun AggregateContext.distances(): Field<Double> =
        environment.getPosition(node).let { nodePosition ->
            neighbouring(nodePosition).map { position -> abs(nodePosition.distanceTo(position)) }
        }

    override fun cloneOnNewNode(node: Node<Any?>): NodeProperty<Any?> =
        CollektiveDevice(environment, node, retainMessagesFor)

    override fun read(): Set<InboundMessage> {
        return validMessages
            .filter { it.receivedAt + retainMessagesFor >= currentTime }
            .also { validMessages = it }
            .map { it.payload }
            .toSet()
    }

    override fun write(message: OutboundMessage) {
        message.messages.forEach { (path, outbound) ->
            environment.getNeighborhood(node)
                .mapNotNull { it.asPropertyOrNull(CollektiveDevice::class) }
                .forEach {
                    it.receiveMessage(
                        currentTime,
                        InboundMessage(
                            message.senderId,
                            mapOf(path to (outbound.overrides[node.toId()] ?: outbound.default)),
                        ),
                    )
                }
        }
    }
}
