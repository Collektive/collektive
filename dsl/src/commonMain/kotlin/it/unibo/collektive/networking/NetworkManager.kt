package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.messages.SentMessage

/**
 * Implementation of the Network interface.
 */
class NetworkManager {
    private var messageBuffer = mutableSetOf<SentMessage>()

    /**
     * Adds the messages to the message buffer.
     * @param messages: the messages to be sent.
     */
    fun send(messages: Set<SentMessage>) {
        messages.forEach { messageBuffer.add(it) }
    }

    /**
     * Returns the messages directed to a specific receiver.
     * @param receiverID: the ID of the receiver.
     * @return the messages received.
     */
    fun receive(receiverID: ID): Set<ReceivedMessage> {
        val filtered = messageBuffer
            .filter {
                (it is AnisotropicMessage && it.receiverID == receiverID) ||
                    (it is IsotropicMessage && it.senderID != receiverID)
            }
            .map { entry -> convertToReceivedMessage(entry) }
            .toSet()
        return filtered
    }

    /**
     * Converts a SentMessage to a ReceivedMessage.
     * @param entry: the SentMessage to be converted.
     */
    private fun convertToReceivedMessage(entry: SentMessage): ReceivedMessage = when (entry) {
        is AnisotropicMessage -> ReceivedMessage(entry.senderID, entry.message)
        is IsotropicMessage -> ReceivedMessage(entry.senderID, entry.message)
    }
}
