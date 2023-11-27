package it.unibo.collektive.network

import it.unibo.collektive.ID
import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.messages.OutboundMessage
import it.unibo.collektive.networking.Network

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: ID) : Network {

    override fun write(message: OutboundMessage) {
        networkManager.send(message)
    }

    override fun read(): Collection<InboundMessage> = networkManager.receive(localId)
}
