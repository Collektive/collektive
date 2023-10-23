package it.unibo.collektive.messages

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Types of messages sent by a device.
 */
sealed interface SentMessage

/**
 * Message sent from a device to other devices in the network.
 * @param senderId: id of the device that sends the message.
 * @param message: map <Path, value> of the message sent.
 */
data class IsotropicMessage(val senderId: ID, val message: Map<Path, *>) : SentMessage

/**
 * Message sent from a device to a specific device in the network.
 * @param senderId: id of the device that sends the message.
 * @param receiverId: id of the device that receives the message.
 * @param message: map <Path, value> of the message sent.
 */
data class AnisotropicMessage(val senderId: ID, val receiverId: ID, val message: Map<Path, *>) : SentMessage
