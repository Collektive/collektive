package it.unibo.collektive.alchemist.device

import it.unibo.alchemist.model.Environment
import it.unibo.alchemist.model.Node
import it.unibo.alchemist.model.NodeProperty
import it.unibo.alchemist.model.Position
import it.unibo.alchemist.model.Time
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field
import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.messages.OutboundMessage
import it.unibo.collektive.networking.Network

/**
 * Collektive device in Alchemist.
 * @param P the position type.
 * @property environment the environment.
 * @property node the node.
 * @property retainMessagesFor the time for which messages are retained.
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

    override fun cloneOnNewNode(node: Node<Any>) = TODO()

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
/*        val actualMessages: Map<> = message.messages.map {  (path, message) ->
            message.
        }
        environment.getNeighborhood(node).
        messages.forEach { message ->
            when (message) {
                is IsotropicMessage ->
                    environment.getNeighborhood(node)
                        .mapNotNull { it.asPropertyOrNull<Any, CollektiveDevice<P>>() }
                        .forEach { it.receiveMessage(currentTime, message.senderId, message.message) }
                is AnisotropicMessage ->
                    environment.getNeighborhood(node)
                        .mapNotNull { it.asPropertyOrNull<Any, CollektiveDevice<P>>() }
                        .firstOrNull { it.node.id == (message.receiverId as IntId).id }
                        ?.also { it.receiveMessage(currentTime, message.senderId, message.message) }
            }
        }*/
    }

    override fun distances(): Field<Double> {
        println(environment)
        TODO()
        //        val res: Map<ID, Double> = mapOf(IntId(node.id) to 0.0) +
//            environment
//                .getNeighborhood(node)
//                .associate { IntId(it.id) to environment.getDistanceBetweenNodes(node, it) }
//        return Field(IntId(node.id), res)
    }
}
