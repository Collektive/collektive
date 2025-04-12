/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.path.Path

/**
 * Factory for creating [Message] instances.
 */
interface MessageFactory<ID : Any, out Payload> {
    /**
     * Creates a [Message] instance with the given [senderId] and [sharedData].
     */
    operator fun invoke(senderId: ID, sharedData: Map<Path, PayloadRepresentation<Any?>>): Message<ID, Payload>
}
