package it.unibo.collektive.aggregate.api.impl

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.some
import it.unibo.collektive.ID
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.aggregate.api.YieldingContext.YieldingResult
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.InboundMessage
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage
import it.unibo.collektive.stack.Path
import it.unibo.collektive.stack.Stack
import it.unibo.collektive.state.State
import it.unibo.collektive.state.getTyped

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

    private val stack = Stack<Any>()
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

    override fun <X> exchange(initial: X, body: (Field<X>) -> Field<X>): Field<X> {
        val messages = messagesAt<X>(stack.currentPath())
        val previous = stateAt(stack.currentPath(), initial)
        val subject = newField(previous, messages)
        return body(subject).also { field ->
            val message = SingleOutboundMessage(field.localValue, field.excludeSelf())
            val path = stack.currentPath()
            check(!toBeSent.messages.containsKey(path)) {
                """
                    Alignment was broken by multiple aligned calls with the same path: $path.
                    The most likely cause is an aggregate function call within a loop
                """.trimIndent()
            }
            toBeSent = toBeSent.copy(messages = toBeSent.messages + (stack.currentPath() to message))
            state = state + (stack.currentPath() to field.localValue)
        }
    }

    override fun <Init, Ret> exchanging(
        initial: Init,
        body: YieldingContext<Field<Init>, Field<Ret>>.(Field<Init>) -> YieldingResult<Field<Init>, Field<Ret>>,
    ): Field<Ret> {
        val messages = messagesAt<Init>(stack.currentPath())
        val previous = stateAt(stack.currentPath(), initial)
        val subject = newField(previous, messages)
        val context = YieldingContext<Field<Init>, Field<Ret>>()
        var res: Option<YieldingResult<Field<Init>, Field<Ret>>>
        body(context, subject).also {
            res = it.some()
            state = state + (stack.currentPath() to it.toReturn.localValue)
        }
        return res.getOrElse { error("This error should never be thrown") }.toReturn
    }

    override fun <Initial, Return> repeating(
        initial: Initial,
        transform: YieldingContext<Initial, Return>.(Initial) -> YieldingResult<Initial, Return>,
    ): Return {
        val context = YieldingContext<Initial, Return>()
        var res: Option<YieldingResult<Initial, Return>>
        transform(context, stateAt(stack.currentPath(), initial)).also {
            res = it.some()
            state = state + (stack.currentPath() to it.toReturn)
        }
        return res.getOrElse { error("This error should never be thrown") }.toReturn
    }

    override fun <Initial> repeat(
        initial: Initial,
        transform: (Initial) -> Initial,
    ): Initial =
        repeating(initial) {
            val res = transform(it)
            YieldingResult(res, res)
        }

    override fun <R> alignedOn(pivot: Any?, body: () -> R): R {
        stack.alignRaw(pivot)
        return body().also { stack.dealign() }
    }

    /**
     * Projects the field to be aligned with the neighbours.
     * A field de-alignment can occur when the field is used inside a body of a branch condition.
     * Take the [field] to project and returns a new field aligned with the neighbours.
     * This method is meant to be used internally by the compiler plugin.
     */
    fun <T> project(field: Field<T>): Field<T> {
        val others = neighbouring(0.toByte())
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> messagesAt(path: Path): Map<ID, T> = messages
        .filter { it.messages.containsKey(path) }
        .associate { it.senderId to it.messages[path] as T }

    private fun <T> stateAt(path: Path, default: T): T = previousState.getTyped(path, default)
}
