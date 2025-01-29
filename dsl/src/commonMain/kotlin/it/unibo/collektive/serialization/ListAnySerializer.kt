/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import it.unibo.collektive.serialization.JsonSerializationUtils.toJsonElement
import it.unibo.collektive.serialization.JsonSerializationUtils.toKotlinType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlin.reflect.KClass

/**
 * A serializer for a list of any objects.
 */
object ListAnySerializer : KSerializer<List<Any?>>, CollektiveTypeSerializer {
    @Serializable
    private abstract class ListAny : List<Any?>

    override val descriptor: SerialDescriptor
        get() = ListAny.serializer().descriptor

    private val registeredTypes = mutableSetOf<KClass<*>>()

    override fun deserialize(decoder: Decoder): List<Any?> =
        when (decoder) {
            is JsonDecoder -> decoder.decodeJsonElement().toKotlinType(registeredTypes) as List<Any?>
            else -> error("Unsupported decoder: $decoder")
        }

    override fun serialize(
        encoder: Encoder,
        value: List<Any?>,
    ) = when (encoder) {
        is JsonEncoder -> encoder.encodeJsonElement(value.toJsonElement())
        else -> error("Unsupported encoder: $encoder")
    }

    override fun <Type : Any> registerType(kClass: KClass<Type>) {
        registeredTypes.add(kClass)
    }
}
