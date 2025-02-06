package it.unibo.collektive.network

import it.unibo.collektive.networking.DeliverableMessage
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.path.Path
import kotlin.reflect.KClass

/**
 * A fully connected virtual network.
 */
class NetworkManager {
    private var messageBuffer: Map<Int, DeliverableMessage<Int, *>> = emptyMap()

    /**
     * Adds the [message] to the message buffer.
     */
    fun send(
        id: Int,
        message: OutboundMessage<Int>,
    ) {
        val deliverableMessage = message.deliverableMessageFor(id)
        messageBuffer += id to deliverableMessage
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receiveMessageFor(receiverId: Int): InboundMessage<Int> =
        object : InboundMessage<Int> {
            private val neighborDeliverableMessages by lazy { messageBuffer.filter { it.key != receiverId } }
            override val neighbors: Set<Int> get() = neighborDeliverableMessages.keys

            @Suppress("UNCHECKED_CAST")
            override fun <Value> dataAt(
                path: Path,
                kClass: KClass<*>,
            ): Map<Int, Value> =
                neighborDeliverableMessages
                    .mapValues { it.value.sharedData.getOrElse(path) { NoValue } as Value }
                    .filter { it.value != NoValue }
        }

    private object NoValue
}
