package it.unibo.collektive

import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.state.State

/**
 * Execute a single cycle of the aggregate computation.
 * @param localId the id of the local node.
 * @param messages the messages received from the other nodes.
 * @param state the state of the local node.
 * @param compute the function that compute the new state of the local node.
 */
fun <X> singleCycle(
    localId: ID,
    messages: Set<ReceivedMessage>,
    state: Set<State<*>>,
    compute: AggregateContext.() -> X,
): AggregateResult<X> {
    return with(AggregateContext(localId, messages, state)) {
        AggregateResult(localId, compute(), messagesToSend(), newState())
    }
}

/**
 * Execute the aggregate computation until the condition is true.
 * @param condition the condition that must be true to continue the computation.
 * @param network the network that is used to communicate with the other nodes.
 * @param compute the function that compute the new state of the local node.
 */
fun <X> runUntil(
    localId: ID,
    condition: () -> Boolean,
    network: Network,
    compute: AggregateContext.() -> X,
): AggregateResult<X> {
    var state = emptySet<State<*>>()
    var computed: AggregateResult<X>? = null
    while (condition()) {
        computed = singleCycle(localId, network.read(), state, compute)
        state = computed.newState
        network.write(computed.toSend)
    }
    return computed ?: error("The computation did not produce a result")
}
