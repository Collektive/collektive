package it.unibo.collektive.network

import it.unibo.collektive.networking.DeliverableMessage
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: Int) : Network<Int> {
    //    override fun write(message: OutboundMessage<Int>) {
//        networkManager.send(message)
//    }
//
//    override fun read(): Collection<InboundMessage<Int>> = networkManager.receive(localId)
    override fun deliverableFor(
        id: Int,
        outboundMessage: OutboundMessage<Int>
    ): DeliverableMessage<Int, *>? {
        TODO("Not yet implemented")
    }

    override fun deliverableReceived(message: DeliverableMessage<Int, *>) {
        TODO("Not yet implemented")
    }

    override fun currentInbound(): InboundMessage<Int> {
        TODO("Not yet implemented")
    }
}
