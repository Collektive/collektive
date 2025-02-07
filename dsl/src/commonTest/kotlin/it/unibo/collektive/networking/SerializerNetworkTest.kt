/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.path.Path
import kotlinx.serialization.json.Json
import kotlin.reflect.KClass

class SerializerNetworkTest : Mailbox<Int> {
    private val serializer = Json
    val messages = mutableMapOf<Int, Message<Int, Any?>>()
    private val factory = object : SerializedMessageFactory<Int, Any?>(serializer) {}

    fun serializeAndSend(id: Int): String {
        require(messages[id] is SerializedMessage<Int>) { "Message $id is not serialized" }
        return serializer.encodeToString(messages[id] as SerializedMessage<Int>)
    }

    fun deserializeAndReceive(serialized: String) {
        val message = serializer.decodeFromString<SerializedMessage<Int>>(serialized)
        deliverableReceived(message)
    }

    override fun deliverableFor(
        id: Int,
        outboundMessage: OutboundEnvelope<Int>,
    ) {
        messages[id] = outboundMessage.prepareMessageFor(id, factory)
    }

    override fun deliverableReceived(message: Message<Int, *>) {
        messages[message.senderId] = message
    }

    override fun currentInbound(): NeighborsData<Int> =
        object : NeighborsData<Int> {
            override val neighbors: Set<Int> get() = messages.keys

            override fun <Value> dataAt(
                path: Path,
                kClass: KClass<*>,
            ): Map<Int, Value> =
                messages
                    .mapValues { (_, message) ->
                        @Suppress("UNCHECKED_CAST")
                        message.sharedData.getOrElse(path) { NoValue } as Value
                    }.filterValues { it != NoValue }
        }

    private object NoValue
}
