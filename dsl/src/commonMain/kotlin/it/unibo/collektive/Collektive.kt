package it.unibo.collektive

import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.impl.AggregateContext
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.state.State

/**
 * Create a Collektive device with a specific [id] and a [network] to manage incoming and outgoing messages,
 * the [computeFunction] is the function to apply within the [AggregateContext].
 */
class Collektive<R>(
    val id: ID,
    private val network: Network,
    private val computeFunction: Aggregate.() -> R,
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
    fun cycleWhile(condition: (AggregateResult<R>) -> Boolean): R {
        var compute = executeRound()
        while (condition(compute)) {
            compute = executeRound()
        }
        return compute.result
    }

    private fun executeRound(): AggregateResult<R> {
        val result = aggregate(id, network, state, computeFunction)
        state = result.newState
        return result
    }

    companion object {

        /**
         * Aggregate program entry point which computes an iteration of a device [localId], taking as parameters
         * the previous [state], the [messages] received from the neighbours and the [compute] with AggregateContext
         * object receiver that provides the aggregate constructs.
         */
        fun <R> aggregate(
            localId: ID,
            inbound: Iterable<InboundMessage> = emptySet(),
            previousState: State = emptyMap(),
            compute: Aggregate.() -> R,
        ): AggregateResult<R> = AggregateContext(localId, inbound, previousState).run {
            AggregateResult(localId, compute(), messagesToSend(), newState())
        }

        /**
         * Aggregate program entry point which computes an iterations of a device [localId],
         * over a [network] of devices, with the lambda [init] with AggregateContext
         * object receiver that provides the aggregate constructs.
         */
        fun <R> aggregate(
            localId: ID,
            network: Network,
            previousState: State = emptyMap(),
            compute: Aggregate.() -> R,
        ): AggregateResult<R> = with(AggregateContext(localId, network.read(), previousState)) {
            AggregateResult(localId, compute(), messagesToSend(), newState()).also {
                network.write(it.toSend)
            }
        }
    }
}
