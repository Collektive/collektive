/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import kotlin.reflect.KClass

/**
 * Interface to make aware the serializer of custom types to be serialized.
 */
interface CollektiveTypeSerializer {
    /**
     * Register a custom [Type] to be serialized.
     */
    fun <Type : Any> registerType(qualifiedName: String, kClass: KClass<Type>)

    /**
     * Register multiple custom types to be serialized.
     */
    fun registerTypes(kClasses: Map<String, KClass<*>>) {
        kClasses.forEach { registerType(it.key, it.value) }
    }
}
