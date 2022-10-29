import Environment.deviceId
import Environment.localFields

typealias ID = Int

class AggregateContext(val messages: Map<Path, Map<ID, *>>, private val previousState: Map<Path, *>) {

    val messagesToSend: Map<Path, *> = TODO()
    val newState: Map<Path, *> = TODO()

    private val state: MutableMap<Path, Any> = mutableMapOf()
    private val toBeSent: MutableMap<Path, Any> = mutableMapOf()
    private val stack: Stack = StackImpl()
    private val localID: ID = TODO()

    private fun <T> messagesAt(path: Path): Map<ID, T> = TODO()

    // nbr
    fun <X : Any> neighbouring(type: X): Field<ID, X> {
        return alignedOn(Token.NEIGHBOURING) { here ->
            toBeSent[here] = type
            val messages = messagesAt<X>(here)
            FieldImpl(Pair(localID, type), messages)
        }
    }

    // rep
    fun repeating(initial: Any?, repeat: (Any?) -> Any): Any {
        return alignedOn(Token.REPEATING) { here ->
            val res = if (previousState.containsKey(here)) repeat(previousState[here]) else repeat(initial)
            state[here] = res
            res
        }
    }

    // share
    fun <X: Any, Y: Any> sharing(init: X, body: (Field<ID, X>) -> Y): Y = alignedOn("share") { here ->
        val messages = messagesAt<X>(here)
        val subject = object : Field<ID, X> {
            override val local: X = init
            override fun toMap(): Map<ID, X> = messages + (localID to init)
            override fun fieldSize() = messages.size + 1
            override fun get(id: ID): X  = messages[id]!!
        }
        body(subject).also {
            toBeSent[here] = it
        }
    }

    private fun <X: Any, Y> alignedOn(token: X, operation: (Path) -> Y) = stack.inNewFrame(token, operation)

    data class AggregateResult<X>(val result: X, val toSend: Map<Path, *>, val newState: Map<Path, *>)

}

fun <X> singleCycle(
    messages: Map<Path, Map<ID, *>> = emptyMap<Path, Map<ID, Any>>(),
    state: Map<Path, *> = emptyMap<Path, Any>(),
    compute: AggregateContext.() -> X
): AggregateContext.AggregateResult<X> = with(AggregateContext(messages, state)) {
    AggregateContext.AggregateResult(compute(), messagesToSend, newState)
}

interface Table // 2-indexed map

interface Network {
    fun send(message: Map<Path, *>): Unit = TODO()
    fun receive(): Map<ID, Map<Path, *>> = TODO()
}

fun <X> runUntil(init: X, network: Network, condition: () -> Boolean, compute: AggregateContext.() -> X): X {
    var state = emptyMap<Path, Any?>()
    var result: X = init
    while (condition()) {
        val messages: Map<Path, Map<ID, *>> = network.receive()
            .flatMap { (id, messages) ->
                messages.toList().map { (path, value) -> Triple(path, id, value) }
            } // List of triples
            .groupBy { it.first } // Map<Path, List<Triple>
            .mapValues { (_, triples) -> triples.map { it.second to it.third }.toMap() }
        val computed = singleCycle(messages, state, compute)
        result = computed.result
        state = computed.newState
        network.send(computed.toSend)
    }
    return result
}

fun <X> aggregate(init: AggregateContext.() -> X) = singleCycle(compute = init).result
