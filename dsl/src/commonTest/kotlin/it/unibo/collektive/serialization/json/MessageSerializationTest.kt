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
import it.unibo.collektive.networking.MessageWithSerializedData
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory
import it.unibo.collektive.serialization.ListAnySerializer
import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialFormat
import kotlinx.serialization.Serializable
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
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
        override val requiresSerialization: Boolean get() = true
        override val neighbors = emptySet<Int>()
        val received: MutableMap<Int, MessageWithSerializedData<Int, String>> = mutableMapOf()

        override fun deliver(outbound: OutboundSendOperation<Int>) {
            neighbors.forEach { id ->
                val message = outbound.messagesFor(id)
                val payloads = message.sharedData.mapValues { (_, value) ->
                    when (serializer) {
                        is StringFormat -> serializer.encodeToString(value).encodeToByteArray()
                        is BinaryFormat -> serializer.encodeToByteArray(outbound)
                        else -> error("E alora serializza tua nonna")
                    }
                }
                val toSend = MessageWithSerializedData(outbound.senderId, payloads)
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
                val serializableMessage: MessageWithSerializedData<Int,String> = when (serializer) {
                    is StringFormat -> serializer.decodeFromString(received.decodeToString())
                    is BinaryFormat -> serializer.decodeFromByteArray(received)
                    else -> error("E alora deserializza tua nonna")
                }
                this.received[serializableMessage.senderId] = serializableMessage
            }
        }

        override fun <T> messageAt(path: Path, kClazz: KClass<*>): Map<Int, T> {
            @OptIn(InternalSerializationApi::class)
            val typeSerializer = kClazz.serializer()
            @Suppress("UNCHECKED_CAST")
            return received.mapValues { (_, serializableMessage) ->
                serializableMessage.serializedSharedData[path]?.let {
                    when (serializer) {
                        is StringFormat -> serializer.decodeFromString(typeSerializer, it.decodeToString()) as T
                        is BinaryFormat -> serializer.decodeFromByteArray(typeSerializer, it) as T
                        else -> error("Unsupported serialization format: $serializer")
                    }
                } ?: NoValue as T
            }.filterValues { it != NoValue }
        }
    }
}

private object NoValue
