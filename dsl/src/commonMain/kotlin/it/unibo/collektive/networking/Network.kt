package it.unibo.collektive.networking

/**
 * Network interface for the aggregate computation.
 */
interface Network<ID : Any> {
    /**
     * Sends a [message] of type [OutboundSendOperation] to the neighbours.
     */
    fun write(message: OutboundSendOperation<ID>)

    /**
     * Returns a set of [Message]s representing the received messages from the neighbours.
     */
    fun read(): Collection<Message<ID>>
}
