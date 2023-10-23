package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.messages.SentMessage
import it.unibo.collektive.messages.SentMessage.Companion.convertToReceivedMessage

/**
 * Implementation of the Network interface.
 */
class NetworkManager {
    private var messageBuffer = setOf<SentMessage>()

    /**
     * Adds the messages to the message buffer.
     * @param messages: the messages to be sent.
     */
    fun send(messages: Set<SentMessage>) {
        messages.forEach { messageBuffer = messageBuffer + it }
    }

    /**
     * Returns the messages directed to a specific receiver.
     * @param receiverId: the ID of the receiver.
     * @return the messages received.
     */
    fun receive(receiverId: ID): Set<ReceivedMessage> {
        val filtered = messageBuffer
            .filter {
                (it is AnisotropicMessage && it.receiverId == receiverId) ||
                    (it is IsotropicMessage && it.senderId != receiverId)
            }
            .map { entry -> convertToReceivedMessage(entry) }
            .toSet()
        return filtered
    }
}
