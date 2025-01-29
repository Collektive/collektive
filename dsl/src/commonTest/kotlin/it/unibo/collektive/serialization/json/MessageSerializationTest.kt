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
import it.unibo.collektive.path.PathFactory
import kotlinx.serialization.Serializable
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
}
