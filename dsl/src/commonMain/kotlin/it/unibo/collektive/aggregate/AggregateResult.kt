package it.unibo.collektive.aggregate

import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.state.State

/**
 * Result of the aggregate computation.
 * It represents the [localId] of the device, the [result] of the computation,
 * the messages [toSend] to other devices and the [newState] of the device.
 */
data class AggregateResult<ID : Any, R>(
    val localId: ID,
    val result: R,
    val toSend: OutboundMessage<ID>,
    val newState: State,
) {
    /**
     * Utilities for the [newState] of the device.
     */
    companion object {
        /**
         * Create an empty [AggregateResult] with the [localId] of the device, the [baseResult] of the computation.
         */
        fun <ID : Any, R> empty(
            localId: ID,
            baseResult: R,
        ): AggregateResult<ID, R> = AggregateResult(localId, baseResult, OutboundMessage(0, localId), emptyMap())
    }
}
