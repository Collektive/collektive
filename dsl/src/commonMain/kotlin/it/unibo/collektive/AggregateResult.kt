package it.unibo.collektive

import it.unibo.collektive.messages.SentMessage
import it.unibo.collektive.state.State

/**
 * Result of the aggregate computation.
 * It represents the [localId] of the device, the [result] of the computation,
 * the messages [toSend] to other devices and the [newState] of the device.
 */
data class AggregateResult<X>(
    val localId: ID,
    val result: X,
    val toSend: Set<SentMessage>,
    val newState: Set<State<*>>,
)
