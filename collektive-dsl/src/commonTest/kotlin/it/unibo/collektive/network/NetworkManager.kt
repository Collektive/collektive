/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.network

import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.path.Path

/**
 * A fully connected virtual network.
 */
class NetworkManager {
    private val messageBuffer: MutableMap<Int, MutableMap<Int, Message<Int, *>>> = mutableMapOf()

    /**
     * Registers a device with the given [deviceId].
     */
    fun registerDevice(deviceId: Int) {
        messageBuffer[deviceId] = mutableMapOf()
    }

    /**
     * Adds the [envelope] to the message buffer.
     */
    fun send(senderId: Int, envelope: OutboundEnvelope<Int>) {
        val neighborsIds = messageBuffer.keys - senderId
        neighborsIds.forEach { neighborId ->
            val message = envelope.prepareMessageFor(senderId)
            messageBuffer[neighborId]?.let { neighborMessages ->
                neighborMessages[senderId] = message
            } ?: run {
                messageBuffer[neighborId] = mutableMapOf(senderId to message)
            }
        }
    }

    /**
     * Return the messages directed to a specific [receiverId].
     */
    fun receiveMessageFor(receiverId: Int): NeighborsData<Int> = object : NeighborsData<Int> {
        private val neighborDeliverableMessages by lazy { messageBuffer[receiverId].orEmpty() }
        override val neighbors: Set<Int> get() = neighborDeliverableMessages.keys

        @Suppress("UNCHECKED_CAST")
        override fun <Value> dataAt(path: Path, dataSharingMethod: DataSharingMethod<Value>): Map<Int, Value> =
            neighborDeliverableMessages
                .mapValues { it.value.sharedData.getOrElse(path) { NoValue } as Value }
                .filter { it.value != NoValue }
    }

    private object NoValue
}
