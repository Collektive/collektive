package it.unibo.collektive.network

import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.OutboundSendOperation

/**
 * A fully connected virtual network.
 */
class NetworkManager {
    private var messageBuffer: Set<OutboundSendOperation<Int>> = emptySet()

    /**
     * Adds the [message] to the message buffer.
     */
    fun send(message: OutboundSendOperation<Int>) {
        messageBuffer = messageBuffer + message
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receive(receiverId: Int): Collection<Message<Int>> =
        messageBuffer
            .filterNot { it.senderId == receiverId }
            .map { it.messagesFor(receiverId) }
}
