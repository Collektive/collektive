package it.unibo.collektive.messages

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Types of messages sent by a device.
 */
sealed interface SentMessage {
    companion object {
        /**
         * Converts a [SentMessage] given as input to a [ReceivedMessage].
         */
        fun convertToReceivedMessage(entry: SentMessage): ReceivedMessage = when (entry) {
            is AnisotropicMessage -> ReceivedMessage(entry.senderId, entry.message)
            is IsotropicMessage -> ReceivedMessage(entry.senderId, entry.message)
        }
    }
}

/**
 * [message] sent from a [senderId] device to all the other devices in the network.
 */
data class IsotropicMessage(val senderId: ID, val message: Map<Path, *>) : SentMessage

/**
 * [message] sent from a [senderId] device to a specific [receiverId] device in the network.
 */
data class AnisotropicMessage(val senderId: ID, val receiverId: ID, val message: Map<Path, *>) : SentMessage
