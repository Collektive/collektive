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
sealed interface Message<ID : Any, Payload> {
    val senderId: ID
    val sharedData: Map<Path, Payload>
}

/**
 * A message specifically designed to be delivered in a in-memory fashion, containing a [senderId] and [sharedData].
 */
data class InMemoryMessage<ID : Any>(
    override val senderId: ID,
    override val sharedData: Map<Path, Any?>,
) : Message<ID, Any?>

/**
 * TODO.
 */
class InMemoryMessageFactory<ID : Any> : MessageFactory<ID, Any?> {
    override fun invoke(
        senderId: ID,
        sharedData: Map<Path, Any?>,
    ): Message<ID, Any?> = InMemoryMessage(senderId, sharedData)
}

/**
 * Serialized message meant to be sent over the network containing a [senderId] and [sharedData].
 */
@Serializable
data class SerializedMessage<ID : Any>(
    override val senderId: ID,
    override val sharedData: Map<Path, ByteArray>,
) : Message<ID, ByteArray>
