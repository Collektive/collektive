/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.serialization

import kotlinx.serialization.Serializable

@Serializable
data class CustomType(val value: String)

@Serializable
data class NonRegisteredType(val value: String)

data class NonSerializableType(val value: String)
