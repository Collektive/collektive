/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.api.impl

import it.unibo.collektive.aggregate.ConstantField
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.InternalAPI
import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.aggregate.api.YieldingResult
import it.unibo.collektive.aggregate.api.YieldingScope
import it.unibo.collektive.aggregate.api.impl.stack.Stack
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.networking.OutboundEnvelope.SharedData
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory
import it.unibo.collektive.state.State
import it.unibo.collektive.state.impl.getTyped

/**
 * Context for managing aggregate computation.
 * It represents the [localId] of the device, the [inboundMessage] received from the neighbors,
 * and the [previousState] of the device.
 */
internal class AggregateContext<ID : Any>(
    override val localId: ID,
    private val inboundMessage: NeighborsData<ID>,
    private val previousState: State,
    override val inMemoryOnly: Boolean = false,
    pathFactory: PathFactory,
) : Aggregate<ID> {
    private val stack = Stack(pathFactory)
    private var state: MutableMap<Path, Any?> = mutableMapOf()
    private val toBeSent: OutboundEnvelope<ID> = OutboundEnvelope(localId, inboundMessage.neighbors.size)

    /**
     * Messages to send to the other nodes.
     */
    fun messagesToSend(): OutboundEnvelope<ID> = toBeSent

    /**
     * Return the current state of the device as a new state.
     */
    fun newState(): State = state

    @CollektiveIgnore(
        """
        This Is a wrapper for the field construction, and must thus not be projected or aligned.
        """,
    )
    private fun <T> newField(localValue: T, others: Map<ID, T>): Field<ID, T> = Field(this, localId, localValue, others)

    @CollektiveIgnore(
        """
        This API internal is not meant to be called directly, and the calling function is always aligned and projected
        """,
    )
    @DelicateCollektiveApi
    override fun <Shared, Returned> InternalAPI.`_ serialization aware exchanging`(
        initial: Shared,
        dataSharingMethod: DataSharingMethod<Shared>,
        body: YieldingScope<Field<ID, Shared>, Returned>,
    ): Returned {
        val path: Path = stack.currentPath()
        val messages = inboundMessage.dataAt(path, dataSharingMethod)
        val previous = stateAt(path, initial)
        val subject: Field<ID, Shared> = newField(previous, messages)
        val context = YieldingContext<Field<ID, Shared>, Returned>()
        return context.body(subject)
            .also {
                val message = SharedData(
                    it.toSend.local.value,
                    when (it.toSend) {
                        is ConstantField<ID, Shared> -> emptyMap()
                        else -> it.toSend.excludeSelf().filterNot { (_, value) -> value == it.toSend.local.value }
                    },
                )
                toBeSent.addData(path, message, dataSharingMethod)
                state += path to it.toSend.local.value
            }.toReturn
    }

    override fun <Stored, Return> evolving(initial: Stored, transform: YieldingScope<Stored, Return>): Return {
        val path = stack.currentPath()
        return transform(YieldingContext(), stateAt(path, initial))
            .also {
                check(it.toReturn !is Field<*, *>) {
                    "evolving operations cannot return fields (guaranteed misalignment on every neighborhood change)"
                }
                state += path to it.toSend
            }.toReturn
    }

    @CollektiveIgnore(
        """
        This API internal is not meant to be called directly, and the calling function is always aligned and projected
        """,
    )
    @DelicateCollektiveApi
    override fun <Scalar> InternalAPI.`_ serialization aware neighboring`(
        local: Scalar,
        dataSharingMethod: DataSharingMethod<Scalar>,
    ): Field<ID, Scalar> {
        val path = stack.currentPath()
        val neighborValues: Map<ID, Scalar> = inboundMessage.dataAt(path, dataSharingMethod)
        toBeSent.addData(path, SharedData(local), dataSharingMethod)
        return newField(local, neighborValues)
    }

    override fun <Stored> evolve(initial: Stored, transform: (Stored) -> Stored): Stored = evolving(initial) {
        val res = transform(it)
        YieldingResult(res, res)
    }

    override fun <R> alignedOn(pivot: Any?, body: () -> R): R {
        stack.alignRaw(pivot)
        return body().also {
            stack.dealign()
        }
    }

    override fun align(pivot: Any?) = stack.alignRaw(pivot)

    override fun dealign() = stack.dealign()

    private fun <T> stateAt(path: Path, default: T): T = previousState.getTyped(path, default)

    override fun toString() = "${this::class.simpleName}@$localId"
}
