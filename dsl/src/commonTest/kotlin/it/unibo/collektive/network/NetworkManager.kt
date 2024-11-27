package it.unibo.collektive.network

import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.OutboundMessage

/**
 * A fully connected virtual network.
 */
class NetworkManager {
    private var messageBuffer: Set<OutboundMessage<Int>> = emptySet()

    /**
     * Adds the [message] to the message buffer.
     */
    fun send(message: OutboundMessage<Int>) {
        messageBuffer = messageBuffer + message
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receive(receiverId: Int): Collection<InboundMessage<Int>> =
        messageBuffer
            .filterNot { it.senderId == receiverId }
            .map { received ->
                InboundMessage(
                    received.senderId,
                    received.messagesFor(receiverId),
                )
            }
}
