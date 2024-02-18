package it.unibo.alchemist.collektive.device

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.Node.Companion.asPropertyOrNull
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
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
    private val retainMessagesFor: Time? = null,
) : NodeProperty<Any?>, Network<Int>, DistanceSensor where P : Position<P> {
    private data class TimedMessage(val receivedAt: Time, val payload: InboundMessage<Int>)

    /**
     * The current time.
     */
    var currentTime: Time = Time.ZERO

    /**
     * The ID of the node.
     */
    val id = node.id

    private val validMessages: MutableList<TimedMessage> = mutableListOf()

    private fun receiveMessage(time: Time, message: InboundMessage<Int>) {
        validMessages += TimedMessage(time, message)
    }

    override fun <ID : Any> Aggregate<ID>.distances(): Field<ID, Double> =
        environment.getPosition(node).let { nodePosition ->
            neighboringViaExchange(nodePosition).map { position -> nodePosition.distanceTo(position) }
        }

    override fun cloneOnNewNode(node: Node<Any?>): NodeProperty<Any?> =
        CollektiveDevice(environment, node, retainMessagesFor)

    override fun read(): Set<InboundMessage<Int>> =
        when (retainMessagesFor) {
            null -> validMessages.mapTo(mutableSetOf()) { it.payload }.also { validMessages.clear() }
            else -> {
                validMessages.retainAll { it.receivedAt + retainMessagesFor >= currentTime }
                validMessages.mapTo(mutableSetOf()) { it.payload }
            }
        }

    override fun write(message: OutboundMessage<Int>) {
        val neighborhood = environment.getNeighborhood(node)
            .mapNotNull { it.asPropertyOrNull<Any?, CollektiveDevice<P>>() }
        val baseMessageBacking = mutableMapOf<Path, Any?>()
        val mayNeedOverrideBacking = mutableMapOf<Path, SingleOutboundMessage<Int, *>>()
        for ((path, payload) in message.messages) {
            if (payload.overrides.isEmpty()) {
                baseMessageBacking[path] = payload.default
            } else {
                mayNeedOverrideBacking[path] = payload
            }
        }
        val baseMessage: Map<Path, Any?> = baseMessageBacking
        val mayNeedOverride: Map<Path, SingleOutboundMessage<Int, *>> = mayNeedOverrideBacking
        neighborhood.forEach { neighbor ->
            val customMessage = InboundMessage(
                message.senderId,
                when {
                    mayNeedOverride.isEmpty() -> baseMessage
                    else -> baseMessage + mayNeedOverride.mapValues { (_, anisotropic) ->
                        anisotropic.overrides.getOrDefault(node.id, anisotropic.default)
                    }
                },
            )
            neighbor.receiveMessage(currentTime, customMessage)
        }
    }
}
