package it.unibo.collektive.networking

import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.messages.SentMessage

/**
 * Network interface for the aggregate computation.
 */
interface Network {
    /**
     * Sends a message to the other nodes.
     * @param messages: the set of messages to be sent.
     */
    fun write(messages: Set<SentMessage>)

    /**
     * Receive the messages sent by the other nodes.
     * @return the set of messages received.
     */
    fun read(): Set<ReceivedMessage>
}
