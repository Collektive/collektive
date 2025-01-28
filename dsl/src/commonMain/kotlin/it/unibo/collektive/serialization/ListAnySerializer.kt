/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal object ListAnySerializer : KSerializer<List<Any?>> {
    @Serializable
    private abstract class ListAny : List<Any?>

    override val descriptor: SerialDescriptor
        get() = ListAny.serializer().descriptor

    override fun deserialize(decoder: Decoder): List<Any?> {
        TODO("Not yet implemented")
    }

    override fun serialize(
        encoder: Encoder,
        value: List<Any?>
    ) {
        TODO("Not yet implemented")
    }
}
