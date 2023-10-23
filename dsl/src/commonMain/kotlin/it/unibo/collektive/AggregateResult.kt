package it.unibo.collektive

import it.unibo.collektive.messages.SentMessage
import it.unibo.collektive.state.State

/**
 * Result of the aggregate computation.
 * @param localId: id of the device.
 * @param result: result of the computation.
 * @param toSend: map with id of the neighbour and relative message to send.
 * @param newState: new state of the device.
 */
data class AggregateResult<X>(
    val localId: ID,
    val result: X,
    val toSend: Set<SentMessage>,
    val newState: Set<State<*>>,
)
