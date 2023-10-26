package it.unibo.collektive.messages

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Types of messages.
 */
sealed interface Message

/**
 * [messages] received by a node from [senderId].
 */
data class InboundMessage(val senderId: ID, val messages: Map<Path, *>) : Message

/**
 * Types of messages sent by a device.
 */
sealed interface OutboundMessage : Message

/**
 * [message] sent from a [senderId] device to all the other devices in the network.
 */
data class IsotropicMessage(val senderId: ID, val message: Map<Path, *>) : OutboundMessage

/**
 * [message] sent from a [senderId] device to a specific [receiverId] device in the network.
 */
data class AnisotropicMessage(val senderId: ID, val receiverId: ID, val message: Map<Path, *>) : OutboundMessage

/**
 * Converts a [OutboundMessage] given as input to a [InboundMessage].
 */
fun OutboundMessage.convertToReceivedMessage(entry: OutboundMessage): InboundMessage = when (entry) {
    is AnisotropicMessage -> InboundMessage(entry.senderId, entry.message)
    is IsotropicMessage -> InboundMessage(entry.senderId, entry.message)
}
