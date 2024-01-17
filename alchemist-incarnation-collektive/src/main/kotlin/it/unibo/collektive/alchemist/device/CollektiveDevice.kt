package it.unibo.collektive.alchemist.device

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Node.Companion.asPropertyOrNull
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.collektive.ID
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.alchemist.utils.toId
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage
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

    private val validMessages: MutableList<TimedMessage> = mutableListOf()

    private fun receiveMessage(
        time: Time,
        message: InboundMessage,
    ) {
        validMessages += TimedMessage(time, message)
    }

    override fun Aggregate.distances(): Field<Double> =
        environment.getPosition(node).let { nodePosition ->
            neighboring(nodePosition).map { position -> nodePosition.distanceTo(position) }
        }

    override fun cloneOnNewNode(node: Node<Any?>): NodeProperty<Any?> =
        CollektiveDevice(environment, node, retainMessagesFor)

    override fun read(): Set<InboundMessage> {
        validMessages.retainAll { it.receivedAt + retainMessagesFor >= currentTime }
        return validMessages.mapTo(mutableSetOf()) { it.payload }
    }

    override fun write(message: OutboundMessage) {
        val neighborhood = environment.getNeighborhood(node)
            .mapNotNull { it.asPropertyOrNull<Any?, CollektiveDevice<P>>() }
        val baseMessageBacking = mutableMapOf<Path, Any?>()
        val mayNeedOverrideBacking = mutableMapOf<Path, SingleOutboundMessage<*>>()
        for ((path, payload) in message.messages) {
            if (payload.overrides.isEmpty()) {
                baseMessageBacking[path] = payload.default
            } else {
                mayNeedOverrideBacking[path] = payload
            }
        }
        val baseMessage: Map<Path, Any?> = baseMessageBacking
        val mayNeedOverride: Map<Path, SingleOutboundMessage<*>> = mayNeedOverrideBacking
        neighborhood.forEach { neighbor ->
            val customMessage = InboundMessage(
                message.senderId,
                when {
                    mayNeedOverride.isEmpty() -> baseMessage
                    else -> baseMessage + mayNeedOverride.mapValues { (_, anisotropic) ->
                        anisotropic.overrides.getOrDefault(node.toId(), anisotropic.default)
                    }
                },
            )
            neighbor.receiveMessage(currentTime, customMessage)
        }
    }
}
