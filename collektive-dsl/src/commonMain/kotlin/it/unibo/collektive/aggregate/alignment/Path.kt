/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment

import it.unibo.collektive.aggregate.alignment.path.IntPath
import it.unibo.collektive.aggregate.alignment.path.LongPath
import it.unibo.collektive.aggregate.alignment.path.StringPath
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * A path represents a specific point in the AST of an aggregate program.
 * The point in the AST is identified as a sequence of tokens.
 */
@Serializable(with = Path.Serializer::class)
interface Path {

    /**
     * Returns a multiline string representation of the path.
     */
    fun toMultilineString(separator: String = "\n"): String = toString()

    /**
     * Custom serializer for [Path], supporting [IntPath], [LongPath], and [StringPath].
     * Paths are encoded as strings (so that they can be used as map keys in any format),
     * prefixed by a one-character type tag, so that deserialization restores the original type:
     * a path received from a neighbor must be equal to the corresponding local one.
     * Serializing any other [Path] implementation throws an [IllegalArgumentException].
     */
    object Serializer : KSerializer<Path> {
        private const val INT_TAG = 'i'
        private const val LONG_TAG = 'l'
        private const val STRING_TAG = 's'
        private const val RADIX = 36

        override val descriptor: SerialDescriptor =
            PrimitiveSerialDescriptor("it.unibo.collektive.aggregate.alignment.Path", PrimitiveKind.STRING)

        override fun serialize(encoder: Encoder, value: Path) {
            val encoded = when (value) {
                is IntPath -> "$INT_TAG${value.backend.toString(RADIX)}"
                is LongPath -> "$LONG_TAG${value.backend.toString(RADIX)}"
                is StringPath -> "$STRING_TAG${value.backend}"
                else -> throw IllegalArgumentException("Cannot serialize $value: unsupported Path type")
            }
            encoder.encodeString(encoded)
        }

        override fun deserialize(decoder: Decoder): Path {
            val encoded = decoder.decodeString()
            val payload = encoded.drop(1)
            return when (encoded.firstOrNull()) {
                INT_TAG -> IntPath(payload.toInt(RADIX))
                LONG_TAG -> LongPath(payload.toLong(RADIX))
                STRING_TAG -> StringPath(payload)
                else -> throw SerializationException("Invalid serialized path: '$encoded'")
            }
        }
    }
}
