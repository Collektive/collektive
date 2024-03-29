package it.unibo.collektive.networking

/**
 * Network interface for the aggregate computation.
 */
interface Network<ID : Any> {
    /**
     * Sends a [message] of type [OutboundMessage] to the neighbours.
     */
    fun write(message: OutboundMessage<ID>)

    /**
     * Returns a set of [InboundMessage]s representing the received messages from the neighbours.
     */
    fun read(): Collection<InboundMessage<ID>>
}
