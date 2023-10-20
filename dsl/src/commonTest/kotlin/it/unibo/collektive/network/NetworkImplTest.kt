package it.unibo.collektive.network

import it.unibo.collektive.ID
import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.messages.SentMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.NetworkManager

open class NetworkImplTest(private val networkManager: NetworkManager, private val localID: ID) : Network {

    override fun write(messages: Set<SentMessage>) {
        networkManager.send(messages)
    }

    override fun read(): Set<ReceivedMessage> = networkManager.receive(localID)
}
