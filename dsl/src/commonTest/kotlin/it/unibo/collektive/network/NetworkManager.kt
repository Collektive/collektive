package it.unibo.collektive.network

import it.unibo.collektive.ID
import it.unibo.collektive.messages.*

/**
 * Implementation of the Network interface.
 */
class NetworkManager {
    private var messageBuffer = setOf<OutboundMessage>()

    /**
     * Adds the [messages] to the message buffer.
     */
    fun send(messages: Set<OutboundMessage>) {
        messages.forEach { messageBuffer = messageBuffer + it }
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receive(receiverId: ID): Set<InboundMessage> {
        val filtered = messageBuffer
            .filter {
                (it is AnisotropicMessage && it.receiverId == receiverId) ||
                    (it is IsotropicMessage && it.senderId != receiverId)
            }
            .map { entry -> entry.convertToReceivedMessage() }
            .toSet()
        return filtered
    }
}
