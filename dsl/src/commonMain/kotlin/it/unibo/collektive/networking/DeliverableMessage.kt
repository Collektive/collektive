/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.path.Path
import kotlinx.serialization.Serializable

/**
 * TODO.
 */
sealed interface DeliverableMessage<ID : Any, Payload> {
    val senderId: ID
    val sharedData: Map<Path, Payload>
}

/**
 * TODO.
 */
data class InMemoryDeliverableMessage<ID : Any, Payload>(
    override val senderId: ID,
    override val sharedData: Map<Path, Payload>,
) : DeliverableMessage<ID, Payload>

/**
 * TODO.
 */
@Serializable
data class SerializedDeliverableMessage<ID : Any>(
    override val senderId: ID,
    override val sharedData: Map<Path, ByteArray>,
) : DeliverableMessage<ID, ByteArray>
