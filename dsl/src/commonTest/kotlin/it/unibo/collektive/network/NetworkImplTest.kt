package it.unibo.collektive.network

import it.unibo.collektive.networking.DeliverableMessage
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: Int) : Network<Int> {
    override fun deliverableFor(
        id: Int,
        outboundMessage: OutboundMessage<Int>,
    ) = networkManager.send(id, outboundMessage)

    override fun deliverableReceived(message: DeliverableMessage<Int, *>) {
        error("This network is supposed to be in-memory, no need to deliver messages since it is already in the buffer")
    }

    override fun currentInbound(): InboundMessage<Int> = networkManager.receiveMessageFor(localId)
}
