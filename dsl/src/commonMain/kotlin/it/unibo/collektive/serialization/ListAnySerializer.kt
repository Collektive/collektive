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
object ListAnySerializer : KSerializer<Any?>, CollektiveTypeSerializer {

    @Serializable
    private abstract class ListAny : List<Any?>

    override val descriptor: SerialDescriptor
        get() = ListAny.serializer().descriptor

    private val registeredTypes = object : MutableMap<String, KClass<*>> by mutableMapOf<String, KClass<*>>() {
        override fun remove(key: String): KClass<*>? = error("Cannot remove types from the registry")
    }
    private val reverseLookupTable = object : Map<KClass<*>, String> {
        private val reversed get() = registeredTypes.entries.associate { (key, value) -> value to key }
        override val size: Int get() = registeredTypes.size
        override val keys: Set<KClass<*>> get() = registeredTypes.values.toSet()
        override val values: Collection<String> get() = registeredTypes.keys
        override val entries: Set<Map.Entry<KClass<*>, String>> get() = reversed.entries
        override fun isEmpty(): Boolean = registeredTypes.isEmpty()
        override fun containsKey(key: KClass<*>): Boolean = registeredTypes.containsValue(key)
        override fun containsValue(value: String): Boolean = registeredTypes.containsKey(value)
        override fun get(key: KClass<*>): String? = registeredTypes.entries.first { it.value == key }.key
    }

    override fun deserialize(decoder: Decoder): Any? =
        when (decoder) {
            is JsonDecoder -> (decoder.decodeJsonElement().toKotlinType(registeredTypes) as List<Any?>).single()
            else -> error("Unsupported decoder: $decoder")
        }

    override fun serialize(
        encoder: Encoder,
        value: Any?,
    ) = when (encoder) {
        is JsonEncoder -> encoder.encodeJsonElement(listOf(value).toJsonElement(reverseLookupTable))
        else -> error("Unsupported encoder: $encoder")
    }

    override fun <Type : Any> registerType(qualifiedName: String, kClass: KClass<Type>) {
        registeredTypes[qualifiedName] = kClass
    }
}
