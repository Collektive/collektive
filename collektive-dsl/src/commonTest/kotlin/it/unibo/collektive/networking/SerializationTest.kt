/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
import it.unibo.collektive.testing.SerializingMailbox
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.protobuf.ProtoBuf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails
import kotlin.test.assertFailsWith

class SerializationTest {
    @Serializable
    data class Custom(val x: Int, val y: Int)

    @Serializable
    data class CustomGeneric<T>(val t: T)

    @Test
    fun `an aggregate output can be serialized when the network supports it`() {
        val mailbox1 = SerializingMailbox()
        aggregate(1, mailbox1, emptyMap()) {
            neighboring(10)
        }
        val serializedMessage = mailbox1.serializeToString(2)
        // Mimic the network receiving a compatible message from a neighbor
        val mailbox2 = SerializingMailbox()
        mailbox2.receiveAndDeserialize(serializedMessage)
        assertEquals(1, mailbox2.receivedMessages.size)
        assertEquals(setOf(1), mailbox2.receivedMessages.keys)
    }

    @Test
    fun `an aggregate output can be serialized when using custom types without exception`() {
        val network = SerializingMailbox()
        aggregate(1, network, emptyMap()) {
            neighboring(Custom(10, 20))
        }
        network.serializeToString(1)
    }

    @Test
    fun `an exception should be raised when a non-serializable type is used`() {
        assertFails {
            val network = SerializingMailbox()
            aggregate(1, network, emptyMap()) {
                neighboring(Any())
            }
            network.serializeToString(1)
        }
    }

    @Test
    fun `a generic data class should be serialized properly`() {
        val network = SerializingMailbox()
        aggregate(1, network, emptyMap()) {
            neighboring(CustomGeneric(localId))
        }
        val serializedMessage = network.serializeToString(1)
        network.receiveAndDeserialize(serializedMessage)
        assertEquals(1, network.receivedMessages.size)
    }

    @Test
    fun `protobuf can be used as a serializer for binary encoded payload`() {
        @OptIn(ExperimentalSerializationApi::class)
        val network = SerializingMailbox(ProtoBuf)
        aggregate(1, network, emptyMap()) {
            neighboring(10)
        }
        val serializedMessage = network.serializeToString(1)
        network.receiveAndDeserialize(serializedMessage)
        assertEquals(1, network.receivedMessages.size)
    }

    @Serializable
    data class MaySerialize<T, K>(val t: T, val k: K)

    private fun <T, K> cantSerializeBecauseOfMissingInlining(t: T, k: K) = aggregate(0, SerializingMailbox(Json)) {
        share(MaySerialize(t, k)) {
            it.local.value
        }
    }

    @Test
    fun `functions without inlining cannot magically serialize`() {
        assertFailsWith<IllegalArgumentException> {
            cantSerializeBecauseOfMissingInlining(listOf(1, 2), mapOf("this is" to 4U))
        }
    }
}
