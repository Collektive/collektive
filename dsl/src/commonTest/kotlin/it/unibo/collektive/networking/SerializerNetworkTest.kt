/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.aggregate.api.Serialize
import it.unibo.collektive.path.Path
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SerializerNetworkTest(private val deviceId: Int, private val serializer: SerialFormat = Json) : Mailbox<Int> {
    override val inMemory: Boolean = false

    val messages = mutableMapOf<Int, Message<Int, Any?>>()
    private val factory = object : SerializedMessageFactory<Int, Any?>(serializer) {}

    fun serializeAndSend(id: Int): String {
        require(messages[id] is SerializedMessage<Int>) { "Message $id is not serialized" }
        return when (serializer) {
            is StringFormat -> serializer.encodeToString(messages[id] as SerializedMessage<Int>)
            is BinaryFormat -> serializer.encodeToByteArray(messages[id] as SerializedMessage<Int>).decodeToString()
            else -> error("Unsupported serializer")
        }
    }

    fun deserializeAndReceive(serialized: String) {
        val message =
            when (serializer) {
                is StringFormat -> serializer.decodeFromString<SerializedMessage<Int>>(serialized)
                is BinaryFormat ->
                    serializer.decodeFromByteArray<SerializedMessage<Int>>(
                        serialized.encodeToByteArray(),
                    )
                else -> error("Unsupported serializer")
            }
        deliverableReceived(message)
    }

    override fun deliverableFor(outboundMessage: OutboundEnvelope<Int>) {
        val neighborIds = messages.keys + deviceId
        for (neighborId in neighborIds) {
            val message = outboundMessage.prepareMessageFor(neighborId, factory)
            messages[neighborId] = message
        }
    }

    override fun deliverableReceived(message: Message<Int, *>) {
        messages[message.senderId] = message
    }

    @OptIn(InternalSerializationApi::class)
    override fun currentInbound(): NeighborsData<Int> =
        object : NeighborsData<Int> {
            override val neighbors: Set<Int> get() = messages.keys

            override fun <Value> dataAt(
                path: Path,
                dataSharingMethod: DataSharingMethod<Value>,
            ): Map<Int, Value> {
                require(dataSharingMethod is Serialize<Value>) {
                    "Serialization has been required for in-memory messages. This is likely a misconfiguration."
                }
                return messages
                    .mapValues { (_, message) ->
                        require(message.sharedData.all { it.value is ByteArray }) {
                            "Message ${message.senderId} is not serialized"
                        }
                        message.sharedData.getOrElse(path) { NoValue }
                    }.filterValues { it != NoValue }
                    .mapValues { (_, payload) ->
                        val byteArrayPayload = payload as ByteArray
                        when (serializer) {
                            is StringFormat ->
                                serializer.decodeFromString(
                                    dataSharingMethod.serializer,
                                    byteArrayPayload.decodeToString(),
                                )
                            is BinaryFormat ->
                                serializer.decodeFromByteArray(
                                    dataSharingMethod.serializer,
                                    byteArrayPayload,
                                )
                            else -> error("Unsupported serializer")
                        }
                    }
            }
        }

    private object NoValue
}
