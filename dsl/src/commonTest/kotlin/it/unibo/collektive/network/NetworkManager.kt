package it.unibo.collektive.network

import it.unibo.collektive.ID
import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.messages.OutboundMessage

/**
 * Implementation of the Network interface.
 */
class NetworkManager {
    private var messageBuffer: Set<OutboundMessage> = emptySet()

    /**
     * Adds the [message] to the message buffer.
     */
    fun send(message: OutboundMessage) {
        messageBuffer = messageBuffer + message
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receive(receiverId: ID): Collection<InboundMessage> = messageBuffer
        .map { received ->
            val sender = received.senderId
            val payloads = received.messages.mapValues { (_, outbound) ->
                outbound.overrides.getOrElse(receiverId) { outbound.default }
            }
            InboundMessage(sender, payloads)
        }
}
