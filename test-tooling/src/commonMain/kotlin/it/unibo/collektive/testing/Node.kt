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
import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.networking.Mailbox
import it.unibo.collektive.networking.Message
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.path.Path

/**
 * A network node with an associated [environment], [id], [value], and [program].
 */
class Node<R>(
    val environment: Environment<R>,
    val id: Int,
    var value: R,
    private val program: Aggregate<Int>.(Environment<R>, Int) -> R,
) {
    private var network = NetworkDevice()

    /**
     * The Collektive instance associated with this node.
     */
    val collektive = Collektive(id, network) { program(environment, id) }

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
    private inner class NetworkDevice : Mailbox<Int> {
        override val inMemory: Boolean = false

        private var messageBuffer: Map<Int, Message<Int, *>> = emptyMap()

        override fun deliverableFor(outboundMessage: OutboundEnvelope<Int>) =
            environment
                .neighborsOf(this@Node)
                .forEach { neighbor ->
                    neighbor.network.messageBuffer += id to outboundMessage.prepareMessageFor(id)
                }

        override fun deliverableReceived(message: Message<Int, *>) {
            error(
                "This network is supposed to be in-memory," +
                    " no need to deliver messages since it is already in the buffer",
            )
        }

        override fun currentInbound(): NeighborsData<Int> = object : NeighborsData<Int> {
            private val neighborDeliverableMessages by lazy { messageBuffer.filter { it.key != id } }
            override val neighbors: Set<Int> get() = neighborDeliverableMessages.keys

            @Suppress("UNCHECKED_CAST")
            override fun <Value> dataAt(path: Path, dataSharingMethod: DataSharingMethod<Value>): Map<Int, Value> =
                neighborDeliverableMessages
                    .mapValues { it.value.sharedData.getOrElse(path) { NoValue } as Value }
                    .filter { it.value != NoValue }
                    .also { messageBuffer = emptyMap() }
        }
    }

    private object NoValue
}
