package it.unibo.collektive.aggregate.api.impl

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.aggregate.api.YieldingResult
import it.unibo.collektive.aggregate.api.YieldingScope
import it.unibo.collektive.aggregate.api.impl.stack.Stack
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.field.ConstantField
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
internal class AggregateContext<ID : Any>(
    override val localId: ID,
    private val messages: Iterable<InboundMessage<ID>>,
    private val previousState: State,
) : Aggregate<ID> {

    private val stack = Stack()
    private var state: MutableMap<Path, Any?> = mutableMapOf()
    private val toBeSent = OutboundMessage(messages.count(), localId)

    /**
     * Messages to send to the other nodes.
     */
    fun messagesToSend(): OutboundMessage<ID> = toBeSent

    /**
     * Return the current state of the device as a new state.
     */
    fun newState(): State = state

    private fun <T> newField(localValue: T, others: Map<ID, T>): Field<ID, T> = Field(localId, localValue, others)

    override fun <X> exchange(initial: X, body: (Field<ID, X>) -> Field<ID, X>): Field<ID, X> =
        exchanging(initial) { field -> body(field).run { yielding { this } } }

    override fun <Init, Ret> exchanging(
        initial: Init,
        body: YieldingScope<Field<ID, Init>, Field<ID, Ret>>,
    ): Field<ID, Ret> {
        val path: Path = stack.currentPath()
        val messages = messagesAt<Init>(path)
        val previous = stateAt(path, initial)
        val subject = newField(previous, messages)
        val context = YieldingContext<Field<ID, Init>, Field<ID, Ret>>()
        return body(context, subject).also {
            val message = SingleOutboundMessage(
                it.toSend.localValue,
                when (it.toSend) {
                    is ConstantField<ID, Init> -> emptyMap()
                    else -> it.toSend.excludeSelf()
                },
            )
            toBeSent.addMessage(path, message)
            state += path to it.toSend.localValue
        }.toReturn
    }

    override fun <Initial, Return> repeating(initial: Initial, transform: YieldingScope<Initial, Return>): Return {
        val path = stack.currentPath()
        return transform(YieldingContext(), stateAt(path, initial))
            .also {
                check(it.toReturn !is Field<*, *>) {
                    "repeating operations cannot return fields (guaranteed misalignment on every neighborhood change)"
                }
                state += path to it.toReturn
            }
            .toReturn
    }

    override fun <Scalar> neighboring(local: Scalar): Field<ID, Scalar> {
        val path = stack.currentPath()
        val neighborValues = messagesAt<Scalar>(path)
        toBeSent.addMessage(path, SingleOutboundMessage(local))
        return newField(local, neighborValues)
    }

    override fun <Initial> repeat(initial: Initial, transform: (Initial) -> Initial): Initial = repeating(initial) {
        val res = transform(it)
        YieldingResult(res, res)
    }

    override fun <R> alignedOn(pivot: Any?, body: () -> R): R {
        stack.alignRaw(pivot)
        return body().also {
            stack.dealign()
        }
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
fun <ID : Any, T> Aggregate<ID>.project(field: Field<ID, T>): Field<ID, T> {
    val others = neighboringViaExchange(0.toByte())
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

/**
 * This function returns true if the Collektive compiler plugin is applied to the current project.
 */
@Suppress("FunctionOnlyReturningConstant")
fun isCompilerPluginApplied(): Boolean = false
