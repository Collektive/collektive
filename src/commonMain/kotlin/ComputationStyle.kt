import stack.Path
import util.switchIndexes

fun <X> singleCycle(
    localId: ID,
    messages: Map<Path, Map<ID, *>> = emptyMap<Path, Map<ID, Any>>(),
    state: Map<Path, *> = emptyMap<Path, Any>(),
    compute: AggregateContext.() -> X
): AggregateContext.AggregateResult<X> = with(AggregateContext(localId, messages, state)) {
    AggregateContext.AggregateResult(compute(), messagesToSend(), newState())
}

fun <X> runUntil(
    condition: () -> Boolean,
    compute: AggregateContext.() -> X): X {
    val localId: ID = IntId()
    val network: Network = NetworkImpl(localId)
    var state = emptyMap<Path, Any?>()
    var result: X? = null
    while (condition()) {
        val messages: Map<Path, Map<ID, *>> = network.receive().switchIndexes()
        val computed = singleCycle(localId, messages, state, compute)
        result = computed.result
        state = computed.newState
        network.send(computed.toSend)
    }
    return result ?: throw IllegalStateException("The computation did not produce a result")
}
