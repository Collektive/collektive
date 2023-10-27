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
 * An [IsotropicMessage] is a [message] that a device [senderId] wants to send to all other neighbours.
 * It is usually sent when devices don't know yet their neighbours, this will also allow them to find new neighbours
 * when they will connect to the network.
 */
data class IsotropicMessage(val senderId: ID, val message: Map<Path, *>) : OutboundMessage

/**
 * An [AnisotropicMessage] is a [message] that a device [senderId] wants to send only to a specific neighbour [receiverId],
 * without being received also from other neighbours.
 */
data class AnisotropicMessage(val senderId: ID, val receiverId: ID, val message: Map<Path, *>) : OutboundMessage

/**
 * Converts a [OutboundMessage] given as input to a [InboundMessage].
 */
fun OutboundMessage.convertToReceivedMessage(): InboundMessage = when (this) {
    is AnisotropicMessage -> InboundMessage(this.senderId, this.message)
    is IsotropicMessage -> InboundMessage(this.senderId, this.message)
}
