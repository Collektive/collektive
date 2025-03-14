package it.unibo.collektive

import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.impl.AggregateContext
import it.unibo.collektive.networking.Mailbox
import it.unibo.collektive.networking.NeighborsData
import it.unibo.collektive.networking.NoNeighborsData
import it.unibo.collektive.path.DigestHashingFactory
import it.unibo.collektive.path.PathFactory
import it.unibo.collektive.state.State

/**
 * Create a Collektive device with a specific [localId] and a [network] to manage incoming and outgoing messages,
 * the [computeFunction] is the function to apply within the [AggregateContext].
 */
class Collektive<ID : Any, R>(
    val localId: ID,
    private val network: Mailbox<ID>,
    private val pathFactory: PathFactory = DigestHashingFactory(),
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
        val result = aggregate(localId, network, state, pathFactory, computeFunction)
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
         */
        fun <ID : Any, R> aggregate(
            localId: ID,
            previousState: State = emptyMap(),
            inbound: NeighborsData<ID> = NoNeighborsData(),
            inMemory: Boolean = inbound is NoNeighborsData,
            pathFactory: PathFactory = DigestHashingFactory(),
            compute: Aggregate<ID>.() -> R,
        ): AggregateResult<ID, R> =
            AggregateContext(localId, inbound, previousState, inMemory, pathFactory).run {
                AggregateResult(localId, compute(), messagesToSend(), newState())
            }

        /**
         * Aggregate program entry point which computes an iteration of a device [localId],
         * over a [network] of devices, optionally from a [previousState],
         * running the [compute] aggregate program.
         */
        fun <ID : Any, R> aggregate(
            localId: ID,
            network: Mailbox<ID>,
            previousState: State = emptyMap(),
            pathFactory: PathFactory = DigestHashingFactory(),
            compute: Aggregate<ID>.() -> R,
        ): AggregateResult<ID, R> =
            with(AggregateContext(localId, network.currentInbound(), previousState, network.inMemory, pathFactory)) {
                AggregateResult(localId, compute(), messagesToSend(), newState()).also {
                    network.deliverableFor(it.toSend)
                }
            }
    }
}
