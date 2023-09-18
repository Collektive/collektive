package it.unibo.collektive

import it.unibo.collektive.stack.Path

/**
 * Execute a single cycle of the aggregate computation.
 * @param localId the id of the local node.
 * @param messages the messages received from the other nodes.
 * @param state the state of the local node.
 * @param compute the function that compute the new state of the local node.
 */
fun <X> singleCycle(
    localId: ID,
    messages: Map<ID, Map<Path, *>>,
    state: Map<Path, *>,
    compute: AggregateContext.() -> X,
): AggregateContext.AggregateResult<X> {
    return with(AggregateContext(localId, messages, state)) {
        AggregateContext.AggregateResult(compute(), messagesToSend(), newState())
    }
}

/**
 * Execute the aggregate computation until the condition is true.
 * @param condition the condition that must be true to continue the computation.
 * @param network the network that is used to communicate with the other nodes.
 * @param compute the function that compute the new state of the local node.
 */
fun <X> runUntil(
    condition: () -> Boolean,
    network: Network,
    compute: AggregateContext.() -> X,
): AggregateContext.AggregateResult<X> {
    val localId: ID = IntId()
    var state = emptyMap<Path, Any?>()
    var computed: AggregateContext.AggregateResult<X>? = null
    while (condition()) {
        computed = singleCycle(localId, network.receive(), state, compute)
        state = computed.newState
        network.send(localId, computed.toSend)
    }
    return computed ?: error("The computation did not produce a result")
}
