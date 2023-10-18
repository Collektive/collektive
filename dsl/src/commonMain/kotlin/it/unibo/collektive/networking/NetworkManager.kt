package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Implementation of the Network interface.
 */
class NetworkManager : Network {
    private var messageBuffer = mutableListOf<Message>()
    private var devices = mutableListOf<ID>()

    override fun send(messages: List<Message>) {
        messages.forEach { messageBuffer.add(it) }
    }

    override fun receive(recipientID: ID): Map<ID, Map<Path, *>> {
        return messageBuffer
            .filter { it.recipientID == recipientID }
            .groupBy { it.senderID }
            .mapValues { entry -> entry.value.flatMap { it.message.entries } }
            .mapValues { entry -> entry.value.associate { it.toPair() } }.also {
                messageBuffer = messageBuffer.filterNot { it.recipientID == recipientID }.toMutableList()
            }
    }

    override fun connectDevice(id: ID) {
        devices.add(id)
    }

    override fun disconnectDevice(id: ID) {
        devices.remove(id)
    }

    override fun getDevices(): List<ID> = devices.toList()
}
