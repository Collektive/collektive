package it.unibo.collektive.networking

import it.unibo.collektive.ID
import it.unibo.collektive.stack.Path

/**
 * Network interface for the aggregate computation.
 */
interface Network {
    /**
     * Sends a message to the other nodes.
     * @param localId id of the device.
     * @param message message to send.
     */
    fun push(messages: List<Message>)

    /**
     * Receive the messages sent by the other nodes.
     * @return a map with the messages sent by the other nodes.
     */
    fun fetch(recipientID: ID): Map<ID, Map<Path, *>>

    /**
     * Add a device to the network's list of devices.
     * @param id: id of the device.
     */
    fun connectDevice(id: ID)

    /**
     * Remove a device from the network's list of devices.
     * @param id: id of the device.
     */
    fun disconnectDevice(id: ID)

    /**
     * Get the list of devices connected to the network.
     * @return list of ID.
     */
    fun getDevices(): List<ID>
}

/**
 * Implementation of the [Network] interface.
 * @param protocolManager: protocol manager used to send and receive messages.
 */
class NetworkImpl(private val protocolManager: ProtocolManager) : Network {
    private var connectedDevices = mutableListOf<ID>()

    override fun push(messages: List<Message>) {
        messages.forEach { protocolManager.sendMessage(it) }
    }

    override fun fetch(recipientID: ID): Map<ID, Map<Path, *>> = protocolManager.receiveMessage(recipientID)

    override fun connectDevice(id: ID) {
        connectedDevices.add(id)
    }

    override fun disconnectDevice(id: ID) {
        connectedDevices.remove(id)
    }

    override fun getDevices(): List<ID> = connectedDevices.toList()
}
