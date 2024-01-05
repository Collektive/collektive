package it.unibo.collektive.aggregate

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.some
import it.unibo.collektive.ID
import it.unibo.collektive.aggregate.ops.RepeatingContext
import it.unibo.collektive.aggregate.ops.RepeatingContext.RepeatingResult
import it.unibo.collektive.aggregate.ops.neighbouring
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
class AggregateContext(
    private val localId: ID,
    private val messages: Iterable<InboundMessage>,
    private val previousState: State,
) {

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

    /**
     * This function computes the local value of e_i, substituting variable n with the nvalue w of
     * messages received from neighbours, using the local value of e_i ([initial]) as a default.
     * The exchange returns the neighbouring or local value v_r from the evaluation of e_r applied to the [body].
     * e_s evaluates to a nvalue w_s consisting of local values to be sent to neighbour devices δ′,
     * which will use their corresponding w_s(δ') as soon as they wake up and perform their next execution round.
     *
     * Often, expressions e_r and e_s coincide, so this function provides a shorthand for exchange(e_i, (n) => (e, e)).
     *
     * ## Example
     * ```
     * exchange(0){ f ->
     *  f.mapField { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
     * }
     * ```
     * The result of the exchange function is a field with as messages a map with key the id of devices across the
     * network and the result of the computation passed as relative local values.
     */
    fun <X> exchange(initial: X, body: (Field<X>) -> Field<X>): Field<X> {
        val messages = messagesAt<X>(stack.currentPath())
        val previous = stateAt(stack.currentPath(), initial)
        val subject = newField(previous, messages)
        return body(subject).also { field ->
            val message = SingleOutboundMessage(field.localValue, field.excludeSelf())
            val path = stack.currentPath()
            check(!toBeSent.messages.containsKey(path)) {
                "Alignment was broken by multiple aligned calls with the same path: $path. " +
                    "The most likely cause is an aggregate function call within a loop"
            }
            toBeSent = toBeSent.copy(messages = toBeSent.messages + (stack.currentPath() to message))
            state = state + (stack.currentPath() to field.localValue)
        }
    }

    /**
     * Iteratively updates the value computing the [transform] expression from a [RepeatingContext]
     * at each device using the last computed value or the [initial].
     */
    fun <Initial, Return> repeating(
        initial: Initial,
        transform: RepeatingContext<Initial, Return>.(Initial) -> RepeatingResult<Initial, Return>,
    ): Return {
        val context = RepeatingContext<Initial, Return>()
        var res: Option<RepeatingResult<Initial, Return>>
        transform(context, stateAt(stack.currentPath(), initial)).also {
            res = it.some()
            state = state + (stack.currentPath() to it.toReturn)
        }
        return res.getOrElse { error("This error should never be thrown") }.toReturn
    }

    /**
     * Iteratively updates the value computing the [transform] expression at each device using the last
     * computed value or the [initial].
     */
    fun <Initial> repeat(
        initial: Initial,
        transform: (Initial) -> Initial,
    ): Initial =
        repeating(initial) {
            val res = transform(it)
            RepeatingResult(res, res)
        }

    /**
     * Alignment function that pushes in the stack the pivot, executes the body and pop the last
     * element of the stack after it is called.
     * Returns the body's return element.
     */
    fun <R> alignedOn(pivot: Any?, body: () -> R): R {
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
