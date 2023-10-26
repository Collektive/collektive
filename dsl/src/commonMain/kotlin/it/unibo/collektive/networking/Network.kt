package it.unibo.collektive.networking

import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.messages.OutboundMessage

/**
 * Network interface for the aggregate computation.
 */
interface Network {
    /**
     * Sends a set of [messages] of type [OutboundMessage] to other nodes.
     */
    fun write(messages: Set<OutboundMessage>)

    /**
     * Receive the messages sent by the other nodes.
     * @return the set of messages received.
     */
    fun read(): Set<InboundMessage>
}
