package it.unibo.collektive.network

import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundSendOperation

class NetworkImplTest(private val networkManager: NetworkManager, private val localId: Int) : Network<Int> {
    override fun write(message: OutboundSendOperation<Int>) {
        networkManager.send(message)
    }

    override fun read(): Collection<Message<Int>> = networkManager.receive(localId)
}
