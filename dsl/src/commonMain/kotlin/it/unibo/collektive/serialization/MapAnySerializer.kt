/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import it.unibo.collektive.path.Path
import it.unibo.collektive.path.impl.StringPath
import it.unibo.collektive.serialization.JsonSerializationUtils.toJsonElement
import it.unibo.collektive.serialization.JsonSerializationUtils.toKotlinType
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * A serializer for a map of paths and any objects.
 */
internal object MapAnySerializer : KSerializer<Map<Path, Any?>>, CollektiveTypeSerializer {
    @Serializable
    private abstract class MapAnyMap : Map<Path, Any?>

    override val descriptor: SerialDescriptor
        get() = MapAnyMap.serializer().descriptor

    private val registeredTypes = mutableSetOf<KClass<*>>()

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): Map<Path, Any?> =
        when (decoder) {
            is JsonDecoder -> {
                val decodedObject = decoder.decodeJsonElement() as JsonObject
                decodedObject
                    .asSequence()
                    .map { (hash, element: JsonElement) ->
                        StringPath(hash) to element.toKotlinType(registeredTypes)
                    }.toMap()
            }
            else -> error("Unsupported decoder: $decoder")
        }

    override fun serialize(
        encoder: Encoder,
        value: Map<Path, Any?>,
    ) = when (encoder) {
        is JsonEncoder ->
            encoder.encodeJsonElement(
                value
                    .map { (path, message) ->
                        check(path is StringPath) {
                            """
                            Serializing a ${path::class} is not supported.
                            Select a different path factory implementation.
                            """.trimIndent()
                        }
                        path.hash to message
                    }.toMap()
                    .toJsonElement(),
            )
        else -> error("Unsupported encoder: $encoder")
    }

    override fun <Type : Any> registerType(kClass: KClass<Type>) {
        registeredTypes.add(kClass)
    }
}
