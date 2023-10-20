package it.unibo.collektive.messages

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * A message received by a node.
 * @param senderId: the ID of the sender.
 * @param messages: the messages received.
 */
data class ReceivedMessage(
    val senderId: ID,
    val messages: Map<Path, *>,
)
