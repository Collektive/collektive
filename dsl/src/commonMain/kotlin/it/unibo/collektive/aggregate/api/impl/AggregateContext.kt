package it.unibo.collektive.aggregate.api.impl

import it.unibo.collektive.ID
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.aggregate.api.YieldingContext.YieldingResult
import it.unibo.collektive.aggregate.api.YieldingScope
import it.unibo.collektive.aggregate.api.impl.stack.Stack
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage
import it.unibo.collektive.path.Path
import it.unibo.collektive.state.State
import it.unibo.collektive.state.impl.getTyped

/**
 * Context for managing aggregate computation.
 * It represents the [localId] of the device, the [messages] received from the neighbours,
 * and the [previousState] of the device.
 */
internal class AggregateContext(
    override val localId: ID,
    private val messages: Iterable<InboundMessage>,
    private val previousState: State,
) : Aggregate {

    private val stack = Stack()
    private var state: State = mapOf()
    private var toBeSent = OutboundMessage(localId, emptyMap())

    /**
     * Messages to send to the other nodes.
     */
    fun messagesToSend(): OutboundMessage = toBeSent

    /**
     * Return the current state of the device as a new state.
     */
    fun newState(): State = state

    private fun <T> newField(localValue: T, others: Map<ID, T>): Field<T> = Field(localId, localValue, others)

    override fun <X> exchange(initial: X, body: (Field<X>) -> Field<X>): Field<X> =
        exchanging(initial) { field -> body(field).let { YieldingResult(it, it) } }

    override fun <Init, Ret> exchanging(initial: Init, body: YieldingScope<Field<Init>, Field<Ret>>): Field<Ret> {
        val messages = messagesAt<Init>(stack.currentPath())
        val previous = stateAt(stack.currentPath(), initial)
        val subject = newField(previous, messages)
        val context = YieldingContext<Field<Init>, Field<Ret>>()
        return body(context, subject).also {
            val message = SingleOutboundMessage(it.toSend.localValue, it.toSend.excludeSelf())
            val path = stack.currentPath()
            check(!toBeSent.messages.containsKey(path)) {
                """
                    Aggregate alignment clash by multiple aligned calls with the same path: $path.
                    The most likely cause is an aggregate function call within a loop
                """.trimIndent()
            }
            toBeSent = toBeSent.copy(messages = toBeSent.messages + (stack.currentPath() to message))
            state += (stack.currentPath() to it.toSend.localValue)
        }.toReturn
    }

    override fun <Initial, Return> repeating(initial: Initial, transform: YieldingScope<Initial, Return>): Return {
        val context = YieldingContext<Initial, Return>()
        val stateAtPath = stateAt(stack.currentPath(), initial)
        return transform(context, stateAtPath).also {
            state += (stack.currentPath() to it.toReturn)
        }.toReturn
    }

    override fun <Initial> repeat(initial: Initial, transform: (Initial) -> Initial): Initial = repeating(initial) {
        val res = transform(it)
        YieldingResult(res, res)
    }

    override fun <R> alignedOn(pivot: Any?, body: () -> R): R {
        stack.alignRaw(pivot)
        return body().also { stack.dealign() }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> messagesAt(path: Path): Map<ID, T> = messages
        .mapNotNull { received ->
            received.messages.getOrElse(path) { NoEntry }
                .takeIf { it != NoEntry }
                ?.let { received.senderId to it as T }
        }
        .associate { it }

    private object NoEntry

    private fun <T> stateAt(path: Path, default: T): T = previousState.getTyped(path, default)
}

/**
 * Projects the field into the current context.
 * This method is meant to be used internally by the Collektive compiler plugin and,
 * unless there is some major bug that needs to be worked around with a kludge,
 * it should never be called, as it incurs in a performance penalty
 * both in computation and message size.
 * A field may be misaligned if captured by a sub-scope which contains an alignment operation.
 * This function takes such [field] and restricts it to be aligned with the current neighbors.
 */
fun <T> Aggregate.project(field: Field<T>): Field<T> {
    val others = neighboring(0.toByte())
    return when {
        field.neighborsCount == others.neighborsCount -> field
        field.neighborsCount > others.neighborsCount -> others.mapWithId { id, _ -> field[id] }
        else -> error(
            """
                Collektive is in an inconsistent state, this is most likely a bug in the implementation.
                Field $field with ${field.neighborsCount} neighbors has been projected into a context
                with more neighbors, ${others.neighborsCount}: ${others.excludeSelf().keys}.
            """.trimIndent().replace(Regex("'\\R"), " "),
        )
    }
}
