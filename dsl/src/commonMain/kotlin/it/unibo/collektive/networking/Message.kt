package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Data class that represents a message.
 * @param senderID: id of the sender.
 * @param recipientID: id of the recipient.
 * @param message: message to send.
 */
data class Message(
    val senderID: ID,
    val recipientID: ID,
    val message: Map<Path, *>,
)
