package it.unibo.collektive.aggregate

import it.unibo.collektive.ID
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.state.State

/**
 * Result of the aggregate computation.
 * It represents the [localId] of the device, the [result] of the computation,
 * the messages [toSend] to other devices and the [newState] of the device.
 */
data class AggregateResult<R>(
    val localId: ID,
    val result: R,
    val toSend: OutboundMessage,
    val newState: State,
)
