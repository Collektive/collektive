/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive

import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.alignment.PerformanceProfile
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.AggregateContext
import it.unibo.collektive.networking.Mailbox
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.NoNeighborsData
import it.unibo.collektive.state.State

/**
 * Create a Collektive device with a specific [localId] and a [network] to manage incoming and outgoing messages,
 * the [computeFunction] is the function to apply within the [AggregateContext].
 * The [performanceProfile] must be chosen explicitly, as no choice fits every deployment:
 * see [PerformanceProfile] for the available trade-offs.
 */
class Collektive<ID : Any, R>(
    val localId: ID,
    private val network: Mailbox<ID>,
    private val performanceProfile: PerformanceProfile,
    private val computeFunction: Aggregate<ID>.() -> R,
) {
    /**
     * The [State] of the Collektive device.
     */
    var state: State = emptyMap()
        private set

    /**
     * Apply once the aggregate function to the parameters of the device,
     * then returns the result of the computation.
     */
    fun cycle(): R = executeRound().result

    /**
     * Apply the aggregate function to the parameters of the device while the [condition] is satisfied,
     * then returns the result of the computation.
     */
    fun cycleWhile(condition: (AggregateResult<ID, R>) -> Boolean): R {
        var compute = executeRound()
        while (condition(compute)) {
            compute = executeRound()
        }
        return compute.result
    }

    private fun executeRound(): AggregateResult<ID, R> {
        val result = aggregate(localId, network, performanceProfile, state, computeFunction)
        state = result.newState
        return result
    }

    /**
     * Global entry points.
     */
    companion object {
        /**
         * Aggregate program entry point which computes an iteration of a device [localId], taking as parameters
         * the [previousState], the [inbound] messages received from the neighbors and the [compute]
         * with AggregateContext receiver that provides the aggregate constructs.
         * Alignment is performed according to the [performanceProfile].
         */
        fun <ID : Any, R> aggregate(
            localId: ID,
            performanceProfile: PerformanceProfile,
            previousState: State = emptyMap(),
            inbound: NeighborsData<ID> = NoNeighborsData(),
            inMemory: Boolean = inbound is NoNeighborsData,
            compute: Aggregate<ID>.() -> R,
        ): AggregateResult<ID, R> =
            AggregateContext(localId, inbound, previousState, inMemory, performanceProfile()).run {
                AggregateResult(localId, compute(), messagesToSend(), state)
            }

        /**
         * Aggregate program entry point which computes an iteration of a device [localId],
         * over a [network] of devices, optionally from a [previousState],
         * running the [compute] aggregate program, aligning according to the [performanceProfile].
         */
        fun <ID : Any, R> aggregate(
            localId: ID,
            network: Mailbox<ID>,
            performanceProfile: PerformanceProfile,
            previousState: State = emptyMap(),
            compute: Aggregate<ID>.() -> R,
        ): AggregateResult<ID, R> = with(
            AggregateContext(
                localId,
                network.currentInbound(),
                previousState,
                network.inMemory,
                performanceProfile(),
            ),
        ) {
            AggregateResult(localId, compute(), messagesToSend(), state).also {
                network.deliverableFor(it.toSend)
            }
        }

        /**
         * Aggregate program entry point which computes an iteration of a device [localId], taking as parameters
         * the [previousState], the [inbound] messages received from the neighbors and the [compute]
         * with AggregateContext receiver that provides the aggregate constructs.
         * Alignment is performed according to the [performanceProfile] available in context, e.g.:
         * ```kotlin
         * context(PerformanceProfile.ForSimulation) {
         *     aggregate(localId) { neighboring(localId) }
         * }
         * ```
         */
        context(performanceProfile: PerformanceProfile)
        fun <ID : Any, R> aggregate(
            localId: ID,
            previousState: State = emptyMap(),
            inbound: NeighborsData<ID> = NoNeighborsData(),
            inMemory: Boolean = inbound is NoNeighborsData,
            compute: Aggregate<ID>.() -> R,
        ): AggregateResult<ID, R> = aggregate(localId, performanceProfile, previousState, inbound, inMemory, compute)

        /**
         * Aggregate program entry point which computes an iteration of a device [localId],
         * over a [network] of devices, optionally from a [previousState],
         * running the [compute] aggregate program,
         * aligning according to the [performanceProfile] available in context.
         */
        context(performanceProfile: PerformanceProfile)
        fun <ID : Any, R> aggregate(
            localId: ID,
            network: Mailbox<ID>,
            previousState: State = emptyMap(),
            compute: Aggregate<ID>.() -> R,
        ): AggregateResult<ID, R> = aggregate(localId, network, performanceProfile, previousState, compute)
    }
}
