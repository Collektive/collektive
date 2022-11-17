import stack.Path
import stack.Stack.currentPath

class AggregateContext(
    private val localId: ID,
    private val messages: Map<Path, Map<ID, *>>,
    private val previousState: Map<Path, *>) {

    private val state: MutableMap<Path, Any> = mutableMapOf()
    private val toBeSent: MutableMap<Path, Any?> = mutableMapOf()

    fun messagesToSend(): Map<Path, *> = toBeSent.toMap()
    fun newState(): Map<Path, *> = state.toMap()

    // nbr
    fun neighbouring(type: Any?): Field<ID, Any?> {
        toBeSent[currentPath()] = type
        val messages = messagesAt(currentPath())
        return FieldImpl(Pair(localId, type), messages)
    }

    // rep
    @Suppress("UNCHECKED_CAST")
    fun <X,Y : Any> repeating(initial: X, repeat: (X) -> Y): Y {
        val res = if (previousState.containsKey(currentPath())) repeat(previousState[currentPath()] as X) else repeat(initial)
        state[currentPath()] = res
        return res
    }

    // share
    fun <X, Y: Any> sharing(initial: X, body: (Field<ID, Any?>) -> Y): Y {
        val messages = messagesAt(currentPath())
        val previous = if (previousState.containsKey(currentPath())) previousState[currentPath()] else initial
        val subject = FieldImpl(Pair(localId, previous), messages)
        return body(subject).also {
            toBeSent[currentPath()] = it
        }
    }

    private fun messagesAt(path: Path): Map<ID, *> = messages[path] ?: emptyMap<ID, Any>()

    data class AggregateResult<X>(val result: X, val toSend: Map<Path, *>, val newState: Map<Path, *>)
}

fun <X> aggregate(init: AggregateContext.() -> X) = singleCycle(IntId(), compute = init).result

fun <X> aggregate(condition: () -> Boolean, network: Network = NetworkImpl(), init: AggregateContext.() -> X) =
    runUntil(condition, network, compute = init)
