/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.aggregate.api.Serialize
import it.unibo.collektive.networking.Mailbox
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.networking.SerializedMessage
import it.unibo.collektive.networking.SerializedMessageFactory
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

/**
 * A [Mailbox] that serializes messages using the provided [serializer].
 * It can be used to test serialization and deserialization of messages.
 *
 * @param serializer The serializer to use for serialization and deserialization.
 */
class SerializingMailbox(private val serializer: SerialFormat = Json) : Mailbox<Int> {
    override val inMemory: Boolean = false

    /**
     * The mailbox that stores the messages.
     * The key is the ID of the sender, and the value is the message.
     */
    val receivedMessages = mutableMapOf<Int, Message<Int, Any?>>()
    private val factory = object : SerializedMessageFactory<Int, Any?>(serializer) {}
    private lateinit var outbound: OutboundEnvelope<Int>

    /**
     * Serializes the message with the given [id] and returns it as a string.
     *
     * @param id The ID of the message to serialize.
     * @return The serialized message as a string.
     */
    fun serializeToString(id: Int): String {
        check(::outbound.isInitialized) {
            "There is no message to be sent. Please call `deliverableFor` first"
        }
        val message = receivedMessages.getOrElse(id) {
            outbound.prepareMessageFor(id, factory)
        }
        check(message is SerializedMessage<*>) {
            "Message $message is not serialized."
        }
        return when (serializer) {
            is StringFormat -> serializer.encodeToString(message as SerializedMessage<Int>)
            is BinaryFormat -> serializer.encodeToByteArray(message as SerializedMessage<Int>).decodeToString()
            else -> error("Unsupported serializer")
        }
    }

    /**
     * Deserializes the given [serialized] message and delivers it to the mailbox.
     * The message must be serialized using the provided [serializer].
     *
     * @param serialized The serialized message to deserialize.
     */
    fun receiveAndDeserialize(serialized: String) {
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
        outbound = outboundMessage
    }

    override fun deliverableReceived(message: Message<Int, *>) {
        receivedMessages[message.senderId] = message
    }

    @OptIn(InternalSerializationApi::class)
    override fun currentInbound(): NeighborsData<Int> = object : NeighborsData<Int> {
        override val neighbors: Set<Int> get() = receivedMessages.keys

        override fun <Value> dataAt(path: Path, dataSharingMethod: DataSharingMethod<Value>): Map<Int, Value> {
            require(dataSharingMethod is Serialize<Value>) {
                "Serialization has been required for in-memory messages. This is likely a misconfiguration."
            }
            return receivedMessages
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

    override fun toString() =
        "${this::class.simpleName}(receivedMessages=$receivedMessages, readyToSend=${::outbound.isInitialized})"
}
