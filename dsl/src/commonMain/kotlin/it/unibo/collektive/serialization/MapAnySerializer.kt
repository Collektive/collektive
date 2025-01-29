/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import it.unibo.collektive.path.Path
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A serializer for a map of paths and any objects.
 */
internal object MapAnySerializer : KSerializer<Map<Path, Any?>> {
    @Serializable
    private abstract class MapAnyMap : Map<Path, Any?>

    override val descriptor: SerialDescriptor
        get() = MapAnyMap.serializer().descriptor

    override fun deserialize(decoder: Decoder): Map<Path, Any?> {
        TODO("Not yet implemented")
    }

    override fun serialize(
        encoder: Encoder,
        value: Map<Path, Any?>,
    ) {
        TODO("Not yet implemented")
    }
}
