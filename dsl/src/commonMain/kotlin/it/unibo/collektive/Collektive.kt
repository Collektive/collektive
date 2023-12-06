package it.unibo.collektive

import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.aggregate.AggregateResult
import it.unibo.collektive.messages.InboundMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.state.State

/**
 * [id] [network] [computeFunction] TODO.
 */
class Collektive<R> (
    private val id: ID,
    private val network: Network,
    private val computeFunction: AggregateContext.() -> R,
) {

    /**
     * TODO.
     */
    var state: State = emptyMap()
        private set

    /**
     * TODO.
     */
    fun cycle(): R = applyAggregate().result

    /**
     * TODO.
     */
    fun cycleWhile(condition: (AggregateResult<R>) -> Boolean): R {
        var compute = applyAggregate()
        while (condition(compute)) {
            compute = applyAggregate()
        }
        return compute.result
    }

    private fun applyAggregate(): AggregateResult<R> {
        val result = aggregate(id, network, state, computeFunction)
        state = result.newState
        return result
    }

    companion object {
        /**
         * TODO.
         */
        operator fun <T> invoke(
            id: ID,
            network: Network,
            funccccc: AggregateContext.() -> T,
        ): Collektive<T> = Collektive(id, network) { funccccc() }

        /**
         * Aggregate program entry point which computes an iteration of a device [localId], taking as parameters
         * the previous [state], the [messages] received from the neighbours and the [compute] with AggregateContext
         * object receiver that provides the aggregate constructs.
         */
        fun <R> aggregate(
            localId: ID,
            inbound: Iterable<InboundMessage> = emptySet(),
            previousState: State = emptyMap(),
            compute: AggregateContext.() -> R,
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
            compute: AggregateContext.() -> R,
        ): AggregateResult<R> = with(AggregateContext(localId, network.read(), previousState)) {
            AggregateResult(localId, compute(), messagesToSend(), newState()).also {
                network.write(it.toSend)
            }
        }
    }
}
