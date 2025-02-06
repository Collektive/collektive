/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.testing

import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.networking.DeliverableMessage
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.path.Path
import kotlin.reflect.KClass

/**
 * A network node with an associated [environment], [id], [value], and [program].
 */
class Node<R>(
    val environment: Environment<R>,
    val id: Int,
    var value: R,
    private val program: Aggregate<Int>.(Environment<R>) -> R,
) {
    private var network = NetworkDevice()

    /**
     * The Collektive instance associated with this node.
     */
    val collektive = Collektive(id, network) { program(environment) }

    /**
     * Runs a Collektive cycle for this node.
     */
    fun cycle() {
        value = collektive.cycle()
    }

    override fun equals(other: Any?) = other is Node<*> && id == other.id

    override fun hashCode() = id

    override fun toString() = "Node($id)"

    /**
     * A network device that can send and receive messages.
     */
    private inner class NetworkDevice : Network<Int> {
        private var messageBuffer: Map<Int, DeliverableMessage<Int, *>> = emptyMap()

        override fun deliverableFor(
            id: Int,
            outboundMessage: OutboundMessage<Int>,
        ) = environment.neighborsOf(this@Node).forEach { neighbor ->
            neighbor.network.messageBuffer += id to outboundMessage.deliverableMessageFor(id)
        }

        override fun deliverableReceived(message: DeliverableMessage<Int, *>) {
            error(
                "This network is supposed to be in-memory," +
                    " no need to deliver messages since it is already in the buffer",
            )
        }

        override fun currentInbound(): InboundMessage<Int> =
            object : InboundMessage<Int> {
                private val neighborDeliverableMessages by lazy { messageBuffer.filter { it.key != id } }
                override val neighbors: Set<Int> get() = neighborDeliverableMessages.keys

                @Suppress("UNCHECKED_CAST")
                override fun <Value> dataAt(
                    path: Path,
                    kClass: KClass<*>,
                ): Map<Int, Value> =
                    neighborDeliverableMessages
                        .mapValues { it.value.sharedData.getOrElse(path) { NoValue } as Value }
                        .filter { it.value != NoValue }
                        .also { messageBuffer = emptyMap() }
            }
    }

    private object NoValue
}
