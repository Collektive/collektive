/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api

import kotlinx.serialization.KSerializer
import kotlin.jvm.JvmInline

/**
 * A method to share data between nodes in a network.
 */
sealed interface DataSharingMethod<in DataType>

/**
 * In-memory share. No serialization. Working only across multiple nodes hosted on the same operating system
 * (for instance, in simulation).
 */
object InMemory : DataSharingMethod<Any?>

/**
 * Serialization-based share using kotlinx.serialization.
 * The data is serialized and deserialized using the provided [serializer].
 */
@JvmInline
value class Serialize<DataType>(val serializer: KSerializer<DataType>) : DataSharingMethod<DataType>
