package it.unibo.collektive.networking

/**
 * TODO.
 */
interface Mailbox<ID : Any> {
    /**
     * True if this mailbox is simple memory sharing. False if it actually serializes messages.
     */
    val inMemory: Boolean

    /**
     * TODO.
     */
    fun deliverableFor(
        id: ID,
        outboundMessage: OutboundEnvelope<ID>,
    )

    /**
     * TODO.
     */
    fun deliverableReceived(message: Message<ID, *>)

    /**
     * TODO.
     */
    fun currentInbound(): NeighborsData<ID>
}
