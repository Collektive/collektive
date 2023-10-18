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
    fun send(messages: List<Message>)

    /**
     * Receive the messages sent by the other nodes.
     * @return a map with the messages sent by the other nodes.
     */
    fun receive(recipientID: ID): Map<ID, Map<Path, *>>

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
