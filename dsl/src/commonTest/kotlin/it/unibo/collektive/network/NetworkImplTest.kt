package it.unibo.collektive.network

import it.unibo.collektive.ID
import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.messages.OutboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.NetworkManager

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: ID) : Network {

    override fun write(messages: Set<OutboundMessage>) {
        networkManager.send(messages)
    }

    override fun read(): Set<InboundMessage> = networkManager.receive(localId)
}
