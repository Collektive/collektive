/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment.path

import it.unibo.collektive.aggregate.alignment.Path
import kotlin.io.encoding.Base64
import kotlin.jvm.JvmInline
import kotlinx.serialization.Serializable

/**
 * Serializable [it.unibo.collektive.aggregate.alignment.Path] implementation that wraps a [backend] object.
 */
@JvmInline
@Serializable
value class StringPath(val backend: String) : Path {

    constructor(bytes: ByteArray) : this(Base64.encode(bytes))
}
