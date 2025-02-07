package it.unibo.collektive.network

import it.unibo.collektive.networking.Mailbox
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: Int) : Mailbox<Int> {
    override fun deliverableFor(
        id: Int,
        outboundMessage: OutboundEnvelope<Int>,
    ) = networkManager.send(id, outboundMessage)

    override fun deliverableReceived(message: Message<Int, *>) {
        error("This network is supposed to be in-memory, no need to deliver messages since it is already in the buffer")
    }

    override fun currentInbound(): NeighborsData<Int> = networkManager.receiveMessageFor(localId)
}
