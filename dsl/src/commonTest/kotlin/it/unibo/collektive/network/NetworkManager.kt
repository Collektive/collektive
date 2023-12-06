package it.unibo.collektive.network

import it.unibo.collektive.ID
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage

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
        .filterNot { it.senderId == receiverId }
        .map { received ->
            InboundMessage(
                received.senderId,
                received.messages.mapValues { (_, single) ->
                    single.overrides.getOrElse(receiverId) { single.default }
                },
            )
        }.also {
            remove(receiverId)
        }

    private fun remove(receiverId: ID) {
        messageBuffer = messageBuffer.mapNotNull { outbound ->
            if (outbound.senderId != receiverId) {
                val newOutbound = OutboundMessage(
                    outbound.senderId,
                    outbound.messages.mapValues { (_, message) ->
                        SingleOutboundMessage(
                            message.default,
                            message.overrides.filterNot { it.key == receiverId },
                        )
                    },
                )
                if (newOutbound.messages.filterNot { it.value.overrides.isEmpty() }.isNotEmpty()) {
                    newOutbound
                } else {
                    null
                }
            } else {
                outbound
            }
        }.toSet()
    }
}
