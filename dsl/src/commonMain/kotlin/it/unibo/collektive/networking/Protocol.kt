package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Protocol interface for collecting messages between networks.
 */
interface Protocol {
    /**
     * Receive the messages sent by the other nodes.
     * @param recipientID: id of the recipient.
     * @return a map with the messages sent by the other nodes.
     */
    fun receiveMessage(recipientID: ID): Map<ID, Map<Path, *>>

    /**
     * Add a message to the list of messages sent by the other nodes.
     * @param message: message to send.
     */
    fun sendMessage(message: Message)
}

/**
 * Implementation of the [Protocol] interface.
 */
class ProtocolManager : Protocol {
    private var messages = mutableListOf<Message>()

    override fun receiveMessage(recipientID: ID): Map<ID, Map<Path, *>> {
        return messages
            .filter { it.recipientID == recipientID }
            .groupBy { it.senderID }
            .mapValues { entry -> entry.value.flatMap { it.message.entries } }
            .mapValues { entry -> entry.value.associate { it.toPair() } }.also {
                messages = messages.filterNot { it.recipientID == recipientID }.toMutableList()
            }
    }

    override fun sendMessage(message: Message) {
        messages.add(message)
    }
}
