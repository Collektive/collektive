/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.networking

import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.path.Path

/**
 * Defined the output of the aggregate program.
 * Holds all the messages to be sent to the neighbors.
 */
interface OutboundEnvelope<ID : Any> {
    /**
     * Shared data holding a [default] value and [overrides] for each [ID].
     */
    data class SharedData<ID : Any, Value>(
        val default: Value,
        val overrides: Map<ID, Value> = emptyMap(),
    )

    /**
     * Adds the [data] generated at the given [path] to the envelope.
     *
     * The [dataSharingMethod] is used to determine how the data should be serialized for network
     * transmission or in-memory delivery.
     */
    fun <Value> addData(
        path: Path,
        data: SharedData<ID, Value>,
        dataSharingMethod: DataSharingMethod<Value>,
    )

    /**
     * Extract the message for the [receiverId] through the given [factory].
     *
     * One the [OutboundEnvelope] is created, this method extracts the message to send to the [receiverId].
     */
    fun prepareMessageFor(
        receiverId: ID,
        factory: MessageFactory<ID, *> = InMemoryMessageFactory(),
    ): Message<ID, Any?>

    /**
     * Returns `true` if the envelope is empty.
     */
    fun isEmpty(): Boolean

    /**
     * Returns `true` if the envelope contains data.
     */
    fun isNotEmpty(): Boolean

    /**
     * Utilities for [OutboundEnvelope].
     */
    companion object {
        /**
         * Smart constructor for [OutboundEnvelope] given a [senderId] and an [expectedSize] for the neighbors.
         */
        internal operator fun <ID : Any> invoke(
            senderId: ID,
            expectedSize: Int,
        ): OutboundEnvelope<ID> =
            object : OutboundEnvelope<ID> {
                private val defaults: MutableMap<Path, PayloadRepresentation<Any?>> = LinkedHashMap(expectedSize * 2)
                private val overrides: MutableMap<ID, MutableList<Pair<Path, PayloadRepresentation<Any?>>>> =
                    LinkedHashMap(expectedSize * 2)

                override fun <Value> addData(
                    path: Path,
                    data: SharedData<ID, Value>,
                    dataSharingMethod: DataSharingMethod<Value>,
                ) {
                    check(!defaults.containsKey(path)) {
                        """
                        Aggregate alignment clash originated at the same path: $path.
                        Possible causes are:
                            - compiler plugin is not enabled,
                            - multiple aligned calls. The most likely cause is an aggregate function call within a loop without proper manual alignment.
                        If none of the above, please open an issue at https://github.com/Collektive/collektive/issues .
                        """.trimIndent()
                    }
                    @Suppress("UNCHECKED_CAST")
                    defaults[path] =
                        PayloadRepresentation(data.default, dataSharingMethod) as PayloadRepresentation<Any?>
                    data.overrides.forEach { (id, value) ->
                        val destination = overrides.getOrPut(id) { mutableListOf() }
                        @Suppress("UNCHECKED_CAST")
                        destination +=
                            path to PayloadRepresentation(value, dataSharingMethod) as PayloadRepresentation<Any?>
                    }
                }

                override fun prepareMessageFor(
                    receiverId: ID,
                    factory: MessageFactory<ID, *>,
                ): Message<ID, Any?> =
                    factory(
                        senderId,
                        overrides[receiverId]?.toMap() ?: defaults,
                    )

                override fun isEmpty(): Boolean = defaults.isEmpty()

                override fun isNotEmpty(): Boolean = defaults.isNotEmpty()
            }
    }
}
