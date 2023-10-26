package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.OutboundMessage
import it.unibo.collektive.messages.convertToReceivedMessage

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
            .map { entry -> entry.convertToReceivedMessage(entry) }
            .toSet()
        return filtered
    }
}
