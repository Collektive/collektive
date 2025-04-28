/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
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
import kotlin.jvm.JvmInline

/**
 * A network node with an associated [environment], [id], [value], and [program].
 */
class Node<R>(
    val environment: Environment<R>,
    val id: Int,
    var value: R,
    private val cyclesToResetMessages: Int = 1,
    private val program: Aggregate<Int>.(Environment<R>) -> R,
) {
    private var network = DeviceMailbox()
    private var lastcycle = 0

    /**
     * The Collektive instance associated with this node.
     */
    val collektive = Collektive(id, network) { program(environment) }

    init {
        require(cyclesToResetMessages >= 1)
    }

    /**
     * Runs a Collektive cycle for this node.
     */
    fun cycle() {
        value = collektive.cycle()
        lastcycle++
    }

    override fun equals(other: Any?) = other is Node<*> && id == other.id

    override fun hashCode() = id

    override fun toString() = "Node($id)"

    @JvmInline
    private value class TimedMessage<T>(private val backend: Pair<Message<Int, T>, Int>) {
        val sharedData get() = message.sharedData
        val message get() = backend.first
        val cycle get() = backend.second
    }

    /**
     * A network device that can send and receive messages.
     */
    private inner class DeviceMailbox : Mailbox<Int> {

        override val inMemory: Boolean = false

        private var messageBuffer: Map<Int, TimedMessage<*>> = emptyMap()

        override fun deliverableFor(outboundMessage: OutboundEnvelope<Int>) = environment
            .neighborsOf(this@Node)
            .forEach { neighbor ->
                val timedMessage = TimedMessage(outboundMessage.prepareMessageFor(neighbor.id) to neighbor.lastcycle)
                neighbor.network.messageBuffer += id to timedMessage
            }

        override fun deliverableReceived(message: Message<Int, *>) {
            error(
                "This network is supposed to be in-memory," +
                    " no need to deliver messages since it is already in the buffer",
            )
        }

        override fun currentInbound(): NeighborsData<Int> = object : NeighborsData<Int> {

            init {
                check(messageBuffer.keys.none { it == id }) {
                    "The message buffer should not contain messages for the sender node"
                }
                // Cleanup the buffer
                messageBuffer = messageBuffer.filterValues {
                    it.cycle > lastcycle - cyclesToResetMessages
                }
            }

            override val neighbors: Set<Int> get() = messageBuffer.keys

            @Suppress("UNCHECKED_CAST")
            override fun <Value> dataAt(path: Path, dataSharingMethod: DataSharingMethod<Value>): Map<Int, Value> =
                messageBuffer
                    .mapValues { it.value.sharedData.getOrElse(path) { NoValue } as Value }
                    .filterValues { it != NoValue }
        }
    }

    private object NoValue
}
