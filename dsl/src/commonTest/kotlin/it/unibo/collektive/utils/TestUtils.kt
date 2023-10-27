package it.unibo.collektive.utils

import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.OutboundMessage
import it.unibo.collektive.stack.Path

/**
 * Returns the paths of a given [OutboundMessage].
 */
fun OutboundMessage.getPaths(): Set<Path> = when (this) {
    is AnisotropicMessage -> this.message.keys
    is IsotropicMessage -> this.message.keys
}
