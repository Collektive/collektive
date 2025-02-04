/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization.json

import it.unibo.collektive.aggregate.api.impl.stack.Stack
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.MessageProvider
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundSendOperation
import it.unibo.collektive.networking.SerializableMessage
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory
import it.unibo.collektive.serialization.ListAnySerializer
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@Serializable
data class Foo(val a: Int, val b: String = "hello")

class MessageSerializationTest {
    @Test
    fun `a message should be serialized and deserialized correctly`() {
        val stack =
            Stack(PathFactory.CryptographicHashingFactory).apply {
                alignRaw(10)
                alignRaw("hello")
            }
        val path = stack.currentPath()
        val message = Message(1, mapOf(path to 10))
        val serializedPath = Json.encodeToString(message)
        val deserializedPath = Json.decodeFromString<Message<Int>>(serializedPath)
        assertEquals(message, deserializedPath)
    }

    class ASDSADAS : Network<Int>, MessageProvider<Int> {
        val serializer: SerialFormat = TODO()
        override val requiresSerialization: Boolean
            get() = true
        override val neighbors = emptySet<Int>()
        val received: MutableMap<Int, SerializableMessage<Int>> = mutableMapOf()

        override fun deliver(outbound: OutboundSendOperation<Int>) {
            neighbors.forEach { id ->
                val message = outbound.messagesFor(id)
                val payloads = message.messages.mapValues { (_, value) ->
                    when (serializer) {
                        is StringFormat -> serializer.encodeToString(value).encodeToByteArray()
                        is BinaryFormat -> serializer.encodeToByteArray(outbound)
                        else -> error("E alora serializza tua nonna")
                    }
                }
                val toSend = SerializableMessage(outbound.senderId, payloads)
                val result: ByteArray = when (serializer) {
                    is StringFormat -> serializer.encodeToString(ListAnySerializer, toSend).encodeToByteArray()
                    is BinaryFormat -> serializer.encodeToByteArray(ListAnySerializer, toSend)
                    else -> error("E alora serializza tua nonna")
                }
                println("send $result to $id")
            }
        }

        private fun incoming(): ByteArray {
            TODO()
        }

        private fun listen() {
            while (true) {
                val received = incoming()
                val serializableMessage: SerializableMessage<Int> = when (serializer) {
                    is StringFormat -> serializer.decodeFromString(received.decodeToString())
                    is BinaryFormat -> serializer.decodeFromByteArray(received)
                    else -> error("E alora deserializza tua nonna")
                }
                this.received[serializableMessage.senderId] = serializableMessage
            }
        }

        fun pluto(id: Int, path: Path): Result<Any?> = runCatching {
            val serializedValue = received.getValue(id).messages.getValue(path)
            when (serializer) {
                is StringFormat -> serializer.decodeFromString(ListAnySerializer, serializedValue.decodeToString())
                is BinaryFormat -> serializer.decodeFromByteArray(ListAnySerializer, serializedValue)
                else -> error("E alora deserializza tua nonna")
            }
        }

        @Suppress("UNCHECKED_CAST")
        override fun <T> messageAt(path: Path): Map<Int, T> {
            return received.mapValues { (_, serializableMessage) ->
                runCatching {
                    if (serializableMessage.messages.containsKey(path)) {
                        val serializedValue = serializableMessage.messages.getValue(path)
                        when (serializer) {
                            is StringFormat -> serializer.decodeFromString(
                                ListAnySerializer,
                                serializedValue.decodeToString()
                            )
                            is BinaryFormat -> serializer.decodeFromByteArray(ListAnySerializer, serializedValue)
                            else -> error("E alora deserializza tua nonna")
                        }
                    } else {
                        NoValue
                    } as T
                }
            }.filterValues { it.isSuccess && it.getOrNull() != NoValue }
                .mapValues { it.value.getOrThrow() }
        }
    }
}

private object NoValue
