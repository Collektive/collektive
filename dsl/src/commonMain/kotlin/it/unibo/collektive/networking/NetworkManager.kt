package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.messages.SentMessage
import it.unibo.collektive.messages.convertToReceivedMessage

/**
 * Implementation of the Network interface.
 */
class NetworkManager {
    private var messageBuffer = setOf<SentMessage>()

    /**
     * Adds the [messages] to the message buffer.
     */
    fun send(messages: Set<SentMessage>) {
        messages.forEach { messageBuffer = messageBuffer + it }
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receive(receiverId: ID): Set<ReceivedMessage> {
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
