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
     * Returns a set of [InboundMessage]s representing the received messages from the neighbours.
     */
    fun read(): Set<InboundMessage>
}
