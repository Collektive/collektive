/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.floatOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.longOrNull
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

/**
 * Utilities for JSON serialization.
 */
object JsonSerializationUtils {
    private const val TYPE_FIELD = "_type"
    private const val DATA_FIELD = "_data"
    private val requiredKeys = setOf(TYPE_FIELD, DATA_FIELD)

    @OptIn(InternalSerializationApi::class)
    internal fun JsonElement.toPrimitive(registeredTypes: Set<KClass<*>>): Any? =
        when (this) {
            is JsonPrimitive -> {
                if (isString) {
                    contentOrNull
                } else {
                    booleanOrNull ?: intOrNull ?: longOrNull ?: floatOrNull ?: doubleOrNull
                }
            }
            is JsonObject -> {
                when {
                    this.keys == requiredKeys -> {
                        val objectContents =
                            requireNotNull(get(DATA_FIELD)) {
                                "Missing required key $DATA_FIELD? $this"
                            }
                        val type: String = get(TYPE_FIELD)?.toPrimitive(registeredTypes).toString()
                        val candidateType = registeredTypes.first { it.qualifiedName == type }
                        Json.decodeFromJsonElement(candidateType.serializer(), objectContents)
                    }
                    else -> toMap()
                }
            }
            is JsonArray -> map { it.toPrimitive(registeredTypes) }
            is JsonNull -> null
        }

    @OptIn(InternalSerializationApi::class)
    internal fun Any?.toJsonElement(): JsonElement =
        when (this) {
            is JsonElement -> this
            is String -> JsonPrimitive(this)
            is Number -> JsonPrimitive(this)
            is Boolean -> JsonPrimitive(this)
            is Map<*, *> -> JsonObject(this.mapKeys { it.key.toString() }.mapValues { it.value.toJsonElement() })
            is Iterable<*> -> JsonArray(this.map { it.toJsonElement() })
            null -> JsonNull
            else -> {
                val clazz: KClass<*> = this::class
                val kSerializer = clazz.serializer()
                kSerializer as KSerializer<Any>
                requireNotNull(clazz.qualifiedName) {
                    "No qualified name found for $clazz"
                }
                JsonObject(
                    mapOf(
                        TYPE_FIELD to JsonPrimitive(clazz.qualifiedName),
                        DATA_FIELD to Json.encodeToJsonElement(kSerializer, this),
                    ),
                )
            }
        }
}
