import field.Field
import field.FieldImpl
import stack.Path
import stack.Stack

class AggregateContext(
    private val localId: ID,
    private val messages: Map<Path, Map<ID, *>>,
    private val previousState: Map<Path, *>) {

    private val stack: Stack<Any> = Stack()
    private val state: MutableMap<Path, Any> = mutableMapOf()
    private val toBeSent: MutableMap<Path, Any?> = mutableMapOf()

    fun messagesToSend(): Map<Path, *> = toBeSent.toMap()
    fun newState(): Map<Path, *> = state.toMap()

    // nbr
    fun <X> neighbouring(type: X): Field<X> {
        val currentPath = stack.currentPath()
        toBeSent[currentPath] = type
        println(currentPath)
        val messages = messagesAt(stack.currentPath())
        return FieldImpl(Pair(localId, type), messages)
    }

    // rep
    @Suppress("UNCHECKED_CAST")
    fun <X,Y : Any> repeating(initial: X, repeat: (X) -> Y): Y {
        val res = if (previousState.containsKey(stack.currentPath())) repeat(previousState[stack.currentPath()] as X) else repeat(initial)
        val currentPath = stack.currentPath()
        println(currentPath)
        state[currentPath] = res
        return res
    }

    // share
    fun <X, Y: Any?> sharing(initial: X, body: (Field<X>) -> Y): Y {
        val messages = messagesAt(stack.currentPath())
        val previous = if (previousState.containsKey(stack.currentPath())) (previousState[stack.currentPath()]) else initial
        val subject = FieldImpl<X>(Pair(localId, previous), messages)
        return body(subject).also {
            val currentPath = stack.currentPath()
            println(currentPath)
            toBeSent[currentPath] = it
        }
    }

    private fun messagesAt(path: Path): Map<ID, *> = messages[path] ?: emptyMap<ID, Any>()

    data class AggregateResult<X>(val result: X, val toSend: Map<Path, *>, val newState: Map<Path, *>)
}

fun <X> aggregate(init: AggregateContext.() -> X) = singleCycle(compute = init).result

fun <X> aggregate(condition: () -> Boolean, network: Network = NetworkImpl(), init: AggregateContext.() -> X) =
    runUntil(condition, network, compute = init)
