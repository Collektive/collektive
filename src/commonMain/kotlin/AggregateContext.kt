import stack.Path
import stack.Stack
import stack.StackImpl
import stack.Token

typealias ID = Int

class AggregateContext(private val messages: Map<Path, Map<ID, *>>, private val previousState: Map<Path, *>) {
    private val state: MutableMap<Path, Any> = mutableMapOf()
    private val toBeSent: MutableMap<Path, Any?> = mutableMapOf()
    private val stack: Stack = StackImpl()
    private val localID: ID = 1

    fun messagesToSend(): Map<Path, *> = toBeSent.toMap()
    fun newState(): Map<Path, *> = state.toMap()

    private fun messagesAt(path: Path): Map<ID, *> = messages[path] ?: emptyMap<ID, Any>()

    // nbr
    fun neighbouring(type: Any?): Field<ID, Any?> {
        return alignedOn(Token.NEIGHBOURING) { here ->
            toBeSent[here] = type
            val messages = messagesAt(here)
            FieldImpl(Pair(localID, type), messages)
        }
    }

    // rep
    @Suppress("UNCHECKED_CAST")
    fun <X,Y : Any> repeating(initial: X, repeat: (X) -> Y): Y {
        return alignedOn(Token.REPEATING) { here ->
            val res = if (previousState.containsKey(here)) repeat(previousState[here] as X) else repeat(initial)
            state[here] = res
            res
        }
    }

    // share
    fun <X, Y: Any> sharing(initial: X, body: (Field<ID, Any?>) -> Y): Y = alignedOn(Token.SHARING) { here ->
        val messages = messagesAt(here)
        val previous = if (previousState.containsKey(here)) previousState[here] else initial
        val subject = FieldImpl(Pair(localID, previous), messages)
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
    AggregateContext.AggregateResult(compute(), messagesToSend(), newState()). also {
        println(messagesToSend())
        println(newState())
    }
}

/*
interface Network {
    fun send(message: Map<stack.Path, *>): Unit = TODO()
    fun receive(): Map<ID, Map<stack.Path, *>> = TODO()
}
*/
fun <X> runUntil(init: X, /*network: Network,*/ condition: () -> Boolean, compute: AggregateContext.() -> X): X {
    var state = emptyMap<Path, Any?>()
    var result: X = init
    while (condition()) {
        /*val messages: Map<stack.Path, Map<ID, *>> = network.receive()
            .flatMap { (id, messages) ->
                messages.toList().map { (path, value) -> Triple(path, id, value) }
            } // List of triples
            .groupBy { it.first } // Map<stack.Path, List<Triple>
            .mapValues { (_, triples) -> triples.map { it.second to it.third }.toMap() }*/
        val computed = singleCycle(emptyMap(), state, compute)
        result = computed.result
        state = computed.newState
        println(state)
        println(result)
        //network.send(computed.toSend)
    }
    return result
}

fun <X> aggregate(init: AggregateContext.() -> X) = singleCycle(compute = init).result

fun <X> aggregate(condition: () -> Boolean, init: AggregateContext.() -> X,) = runUntil(0, condition, compute = init)
