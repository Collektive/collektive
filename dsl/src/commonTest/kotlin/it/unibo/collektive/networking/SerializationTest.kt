/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.neighboring
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class SerializationTest {
    @Serializable
    data class Custom(val x: Int, val y: Int)

    @Serializable
    data class CustomGeneric<T>(val t: T)

    @Test
    fun `an aggregate output can be serialized when the used network support it`() {
        val network = SerializerNetworkTest(1)
        aggregate(1, network, emptyMap()) {
            neighboring(10)
        }
        val serializedMessage = network.serializeAndSend(1)
        // Mimic the network receiving a compatible message from a neighbor
        val neighborMessage = serializedMessage.replace("\"senderId\":1", "\"senderId\":10")
        network.deserializeAndReceive(neighborMessage)
        assertEquals(2, network.messages.size)
        assertEquals(setOf(1, 10), network.messages.keys)
    }

    @Test
    fun `an aggregate output can be serialized when using custom types without exception`() {
        val network = SerializerNetworkTest(1)
        aggregate(1, network, emptyMap()) {
            neighboring(Custom(10, 20))
        }
        network.serializeAndSend(1)
    }

    @Test
    fun `an exception should be raised when a non-serializable type is used`() {
        assertFails {
            val network = SerializerNetworkTest(1)
            aggregate(1, network, emptyMap()) {
                neighboring(Any())
            }
            network.serializeAndSend(1)
        }
    }

    @Test
    fun `a generic data class should be serialized properly`() {
        val network = SerializerNetworkTest(1)
        aggregate(1, network, emptyMap()) {
            neighboring(CustomGeneric(localId))
        }
        val serializedMessage = network.serializeAndSend(1)
        network.deserializeAndReceive(serializedMessage)
        assertEquals(1, network.messages.size)
    }

    @Test
    fun `protobuf can be used as a serializer for binary encoded payload`() {
        @OptIn(ExperimentalSerializationApi::class)
        val network = SerializerNetworkTest(1, ProtoBuf)
        aggregate(1, network, emptyMap()) {
            neighboring(10)
        }
        val serializedMessage = network.serializeAndSend(1)
        network.deserializeAndReceive(serializedMessage)
        assertEquals(1, network.messages.size)
    }
}
