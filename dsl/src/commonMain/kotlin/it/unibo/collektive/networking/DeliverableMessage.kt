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
 * A message meant to be delivered in a communication medium, containing a [senderId] and [sharedData].
 */
sealed interface DeliverableMessage<ID : Any, Payload> {
    val senderId: ID
    val sharedData: Map<Path, Payload>
}

/**
 * A message specifically designed to be delivered in a in-memory fashion, containing a [senderId] and [sharedData].
 */
data class InMemoryDeliverableMessage<ID : Any>(
    override val senderId: ID,
    override val sharedData: Map<Path, Any?>,
) : DeliverableMessage<ID, Any?>

/**
 * TODO.
 */
class InMemoryDeliverableMessageFactory<ID : Any> : DeliverableMessageFactory<ID, Any?> {
    override fun invoke(
        senderId: ID,
        sharedData: Map<Path, Any?>,
    ): DeliverableMessage<ID, Any?> = InMemoryDeliverableMessage(senderId, sharedData)
}

/**
 * Serialized message meant to be sent over the network containing a [senderId] and [sharedData].
 */
@Serializable
data class SerializedDeliverableMessage<ID : Any>(
    override val senderId: ID,
    override val sharedData: Map<Path, ByteArray>,
) : DeliverableMessage<ID, ByteArray>
