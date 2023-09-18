package it.unibo.collektive

import it.unibo.collektive.stack.Path

/**
 * Network interface for the aggregate computation.
 */
interface Network {
    /**
     * Sends a message to the other nodes.
     * @param localId: id of the device.
     * @param message: message to send.
     */
    fun send(localId: ID, message: Map<Path, *>)

    /**
     * Receive the messages sent by the other nodes.
     * @return a map with the messages sent by the other nodes.
     */
    fun receive(): Map<ID, Map<Path, *>>
}

/**
 * Implementation of the [Network] interface.
 */
class NetworkImpl : Network {
    private val sentMessages: MutableMap<ID, Map<Path, *>> = mutableMapOf()

    override fun send(localId: ID, message: Map<Path, *>) {
        sentMessages[localId] = message
    }

    override fun receive(): Map<ID, Map<Path, *>> = sentMessages.toMap()
}
