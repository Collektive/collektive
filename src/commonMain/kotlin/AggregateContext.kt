import stack.Path
import stack.Stack
import stack.StackImpl
import stack.Token

class AggregateContext(
    private val localId: ID,
    private val messages: Map<Path, Map<ID, *>>,
    private val previousState: Map<Path, *>) {

    private val state: MutableMap<Path, Any> = mutableMapOf()
    private val toBeSent: MutableMap<Path, Any?> = mutableMapOf()
    private val stack: Stack = StackImpl()

    fun messagesToSend(): Map<Path, *> = toBeSent.toMap()
    fun newState(): Map<Path, *> = state.toMap()

    // nbr
    fun neighbouring(type: Any?): Field<ID, Any?> {
        return alignedOn(Token.NEIGHBOURING) { here ->
            toBeSent[here] = type
            val messages = messagesAt(here)
            FieldImpl(Pair(localId, type), messages)
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
        val subject = FieldImpl(Pair(localId, previous), messages)
        body(subject).also {
            toBeSent[here] = it
        }
    }

    private fun messagesAt(path: Path): Map<ID, *> = messages[path] ?: emptyMap<ID, Any>()
    private fun <X: Any, Y> alignedOn(token: X, operation: (Path) -> Y) = stack.inNewFrame(token, operation)

    data class AggregateResult<X>(val result: X, val toSend: Map<Path, *>, val newState: Map<Path, *>)
}

fun <X> aggregate(init: AggregateContext.() -> X) = singleCycle(IntId(), compute = init).result

fun <X> aggregate(condition: () -> Boolean, network: Network = NetworkImpl(), init: AggregateContext.() -> X) =
    runUntil(condition, network, compute = init)
