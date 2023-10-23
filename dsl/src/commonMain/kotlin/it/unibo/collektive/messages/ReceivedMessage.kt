package it.unibo.collektive.messages

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * [messages] received by a node from [senderId].
 */
data class ReceivedMessage(
    val senderId: ID,
    val messages: Map<Path, *>,
)
