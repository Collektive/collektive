package it.unibo.collektive.networking

/**
 * TODO.
 */
interface Mailbox<ID : Any> {
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
