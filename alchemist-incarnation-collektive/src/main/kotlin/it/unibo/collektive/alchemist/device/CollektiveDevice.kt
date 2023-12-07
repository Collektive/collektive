package it.unibo.collektive.alchemist.device

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage

/**
 * Representation of a Collektive device in Alchemist.
 * [P] is the position type, the [environment] property represent the environment in which the device is located,
 * the [node] property represent a node in the environment, [retainMessagesFor] is the time for which messages
 * are retained.
 */
class CollektiveDevice<P>(
    private val environment: Environment<Any, P>,
    override val node: Node<Any>,
    private val retainMessagesFor: Time,
) : NodeProperty<Any>, Network, DistanceSensor where P : Position<P> {

    private data class TimedMessage(val receivedAt: Time, val payload: InboundMessage)

    /**
     * The current time.
     */
    var currentTime: Time = Time.ZERO

    private var validMessages: Iterable<TimedMessage> = emptySet()

    private fun receiveMessage(time: Time, message: InboundMessage) {
        validMessages += TimedMessage(time, message)
    }

    override fun cloneOnNewNode(node: Node<Any>) = TODO("Not yet implemented")

    override fun read(): Set<InboundMessage> {
        return validMessages
            .filter { it.receivedAt + retainMessagesFor >= currentTime }
            .also { validMessages = it }
            .map { it.payload }
            .toSet()
    }

    override fun write(message: OutboundMessage) {
        message.messages.mapValues { (path, outbound) ->
            receiveMessage(
                currentTime,
                InboundMessage(
                    message.senderId,
                    mapOf(path to outbound.overrides.getOrElse(IntId(node.id)) { outbound.default }),
                ),
            )
        }
    }

    override fun distances(): Field<Double> {
        println(environment)
        TODO("Not yet implemented")
    }
}
