/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */
package it.unibo.collektive.aggregate.api.impl

import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.none
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.InternalAPI
import it.unibo.collektive.aggregate.api.DataSharingMethod
import it.unibo.collektive.aggregate.api.DelicateCollektiveApi
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.aggregate.api.YieldingResult
import it.unibo.collektive.aggregate.api.YieldingScope
import it.unibo.collektive.aggregate.api.impl.stack.Stack
import it.unibo.collektive.aggregate.api.neighborhood
import it.unibo.collektive.field.ConstantField
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.OutboundEnvelope
import it.unibo.collektive.networking.OutboundEnvelope.SharedData
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory
import it.unibo.collektive.state.State
import it.unibo.collektive.state.impl.getTyped

/**
 * Context for managing aggregate computation.
 * It represents the [localId] of the device, the [inboundMessage] received from the neighbours,
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

    private fun <T> newField(localValue: T, others: Map<ID, T>): Field<ID, T> = Field(localId, localValue, others)

    @DelicateCollektiveApi
    override fun <Shared, Returned> InternalAPI.`_ serialization aware exchanging`(
        initial: Field<ID, Shared>,
        dataSharingMethod: DataSharingMethod<Shared>,
        body: YieldingScope<Field<ID, Shared>, Returned>,
    ): Returned {
        val path: Path = stack.currentPath()
        val messages = inboundMessage.dataAt<Shared>(path, dataSharingMethod)
        val previous: Option<Shared> = stateAt(path, none())
        val subject: Field<ID, Shared> = when (previous) {
            is None -> initial
            is Some<Shared> -> newField(previous.value, messages)
        }
        val context = YieldingContext<Field<ID, Shared>, Returned>()
        return context.body(subject)
            .also {
                val message = SharedData(
                    it.toSend.localValue,
                    when (it.toSend) {
                        is ConstantField<ID, Shared> -> emptyMap()
                        else -> it.toSend.excludeSelf().filterNot { (_, value) -> value == it.toSend.localValue }
                    },
                )
                toBeSent.addData(path, message, dataSharingMethod)
                state += path to it.toSend.localValue
            }.toReturn
    }

    @DelicateCollektiveApi
    override fun <Shared, Returned> InternalAPI.`_ serialization aware exchanging`(
        initial: Shared,
        dataSharingMethod: DataSharingMethod<Shared>,
        body: YieldingScope<Field<ID, Shared>, Returned>,
    ) = `_ serialization aware exchanging`(newField(initial, emptyMap()), dataSharingMethod, body)

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

/**
 * Projects the field into the current context, restricting the field to the current context.
 *
 * A field may be misaligned if captured by a sub-scope which contains an alignment operation.
 * This function takes such [field] and restricts it to be aligned with the current neighbors.
 *
 * This method is meant to be used internally by the Collektive compiler plugin
 * and should never be called from the outside.
 *
 * If you happen to call it, be aware that you are probably building a terrible kludge that will
 * break sooner or later.
 *
 */
@DelicateCollektiveApi
fun <ID : Any, T> Aggregate<ID>.project(field: Field<ID, T>): Field<ID, T> {
    val others = neighborhood()
    return when {
        field.neighborsCount == others.neighborsCount -> field
        field.neighborsCount > others.neighborsCount -> others.mapWithId { id, _ -> field[id] }
        else ->
            error(
                """
                Collektive is in an inconsistent state, this is most likely a bug in the implementation.
                Field $field with ${field.neighborsCount} neighbors has been projected into a context
                with more neighbors, ${others.neighborsCount}: ${others.excludeSelf().keys}.
                """.trimIndent().replace(Regex("'\\R"), " "),
            )
    }
}
