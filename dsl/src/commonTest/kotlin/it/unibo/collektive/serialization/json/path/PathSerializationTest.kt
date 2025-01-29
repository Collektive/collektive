/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization.json.path

import it.unibo.collektive.path.impl.FullPath
import it.unibo.collektive.serialization.CustomType
import it.unibo.collektive.serialization.ListAnySerializer
import it.unibo.collektive.serialization.NonRegisteredType
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFails

class PathSerializationTest {
    @Test
    fun `an empty path should be serialized and deserialized correctly`() {
        val emptyPath = FullPath(emptyList())
        val jsonEncodedPath = Json.encodeToString(emptyPath)
        assertEquals("""{"path":[]}""", jsonEncodedPath)
        val jsonDecodedPath = Json.decodeFromString<FullPath>(jsonEncodedPath)
        assertEquals(emptyPath, jsonDecodedPath)
    }

    @Test
    fun `a path with one element should be serialized and deserialized correctly`() {
        val path = FullPath(listOf("a"))
        val jsonEncodedPath = Json.encodeToString(path)
        assertEquals("""{"path":["a"]}""", jsonEncodedPath)
        val jsonDecodedPath = Json.decodeFromString<FullPath>(jsonEncodedPath)
        assertEquals(path, jsonDecodedPath)
    }

    @Test
    fun `a path with primitive heterogeneous elements as a token should serialize and deserialize correctly`() {
        val path = FullPath(listOf("a", 1, 2.0, true))
        val jsonEncodedPath = Json.encodeToString(path)
        assertEquals("""{"path":["a",1,2.0,true]}""", jsonEncodedPath)
        val jsonDecodedPath = Json.decodeFromString<FullPath>(jsonEncodedPath)
        assertEquals(path, jsonDecodedPath)
    }

    @Test
    fun `a path with a custom type as a token should serialize and deserialize correctly`() {
        ListAnySerializer.registerType(CustomType::class)
        val path = FullPath(listOf("a", CustomType("b")))
        val jsonEncodedPath = Json.encodeToString(path)
        assertEquals(
            """{"path":["a",{"_type":"it.unibo.collektive.serialization.CustomType","_data":{"value":"b"}}]}""",
            jsonEncodedPath,
        )
        val jsonDecodedPath = Json.decodeFromString<FullPath>(jsonEncodedPath)
        assertEquals(path, jsonDecodedPath)
    }

    @Test
    fun `an exception should be thrown when trying to deserialize a path with an unknown type`() {
        val path = FullPath(listOf("a", NonRegisteredType("b")))
        val jsonEncodedPath = Json.encodeToString(path)
        assertFails {
            Json.decodeFromString<FullPath>(jsonEncodedPath)
        }
    }
}
