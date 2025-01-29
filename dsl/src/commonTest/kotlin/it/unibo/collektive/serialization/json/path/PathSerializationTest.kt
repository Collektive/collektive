/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization.json.path

import it.unibo.collektive.path.impl.PathImpl
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class PathSerializationTest {
    @Test
    fun `an empty path should be serialized and deserialized correctly`() {
        val emptyPath = PathImpl(emptyList())
        val jsonEncodedPath = Json.encodeToString(emptyPath)
        assertEquals("""{"path":[]}""", jsonEncodedPath)
        val jsonDecodedPath = Json.decodeFromString<PathImpl>(jsonEncodedPath)
        assertEquals(emptyPath, jsonDecodedPath)
    }

    @Test
    fun `a path with one element should be serialized and deserialized correctly`() {
        val path = PathImpl(listOf("a"))
        val jsonEncodedPath = Json.encodeToString(path)
        assertEquals("""{"path":["a"]}""", jsonEncodedPath)
        val jsonDecodedPath = Json.decodeFromString<PathImpl>(jsonEncodedPath)
        assertEquals(path, jsonDecodedPath)
    }

    @Test
    fun `a path with primitive heterogeneous elements as a token should serialize and deserialize correctly`() {
        val path = PathImpl(listOf("a", 1, 2.0, true))
        val jsonEncodedPath = Json.encodeToString(path)
        assertEquals("""{"path":["a",1,2.0,true]}""", jsonEncodedPath)
        val jsonDecodedPath = Json.decodeFromString<PathImpl>(jsonEncodedPath)
        assertEquals(path, jsonDecodedPath)
    }
}
