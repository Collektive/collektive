package it.unibo.collektive.networking

/**
 * TODO.
 */
interface Network<ID : Any> {
    /**
     * TODO.
     */
    fun deliverableFor(
        id: ID,
        outboundMessage: OutboundMessage<ID>,
    )

    /**
     * TODO.
     */
    fun deliverableReceived(message: DeliverableMessage<ID, *>)

    /**
     * TODO.
     */
    fun currentInbound(): InboundMessage<ID>
}
