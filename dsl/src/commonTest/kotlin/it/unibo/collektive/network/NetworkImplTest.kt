package it.unibo.collektive.network

import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: Int) : Network<Int> {
    override fun write(message: OutboundMessage<Int>) {
        networkManager.send(message)
    }

    override fun read(): Collection<InboundMessage<Int>> = networkManager.receive(localId)
}
