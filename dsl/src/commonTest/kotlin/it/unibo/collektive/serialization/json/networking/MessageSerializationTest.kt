/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization.json.networking

import kotlinx.serialization.Serializable
import kotlin.test.Test

@Serializable
data class Foo(val a: Int, val b: String = "hello")

class MessageSerializationTest {
    @Test
    fun `a message should be serialized and deserialized correctly`() {
//        val value = mapOf<Foo, String>(Foo(1) to "hello", Foo(2) to "world")
//        println(Json.encodeToString(value))
//
//        val message = Message(1, mapOf(Path("a", "f") to 10, Path("b") to "hello"))
//        val jsonEncodedMessage = Json.encodeToString(message)
//        val jsonDecodedMessage = Json.decodeFromString<Message<Int>>(jsonEncodedMessage)
//        jsonDecodedMessage.messages.forEach { (path, value) ->
//            println("$path -> ${value!!::class.qualifiedName}")
//        }
//        assertEquals(message, jsonDecodedMessage)
    }
}
