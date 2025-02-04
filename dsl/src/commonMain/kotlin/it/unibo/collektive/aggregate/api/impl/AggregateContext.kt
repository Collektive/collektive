package it.unibo.collektive.aggregate.api.impl

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.neighboring
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.aggregate.api.YieldingResult
import it.unibo.collektive.aggregate.api.YieldingScope
import it.unibo.collektive.aggregate.api.impl.stack.Stack
import it.unibo.collektive.field.ConstantField
import it.unibo.collektive.field.Field
import it.unibo.collektive.networking.MessageProvider
import it.unibo.collektive.networking.OutboundSendOperation
import it.unibo.collektive.networking.SingleOutboundMessage
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory
import it.unibo.collektive.serialization.ListAnySerializer
import it.unibo.collektive.state.State
import it.unibo.collektive.state.impl.getTyped
import kotlin.reflect.KClass

/**
 * Context for managing aggregate computation.
 * It represents the [localId] of the device, the [messageProvider] received from the neighbours,
 * and the [previousState] of the device.
 */
internal class AggregateContext<ID : Any>(
    override val localId: ID,
    private val messageProvider: MessageProvider<ID>,
    private val previousState: State,
    pathFactory: PathFactory,
) : Aggregate<ID> {
    private val stack = Stack(pathFactory)
    private var state: MutableMap<Path, Any?> = mutableMapOf()
    private val toBeSent = OutboundSendOperation(messageProvider.neighbors.size, localId)

    /**
     * Messages to send to the other nodes.
     */
    fun messagesToSend(): OutboundSendOperation<ID> = toBeSent

    /**
     * Return the current state of the device as a new state.
     */
    fun newState(): State = state

    private fun <T> newField(
        localValue: T,
        others: Map<ID, T>,
    ): Field<ID, T> = Field(localId, localValue, others)

    override fun <Initial : Any> exchange(
        initial: Initial?,
        kClazz: KClass<Initial>,
        body: (Field<ID, Initial?>) -> Field<ID, Initial?>,
    ): Field<ID, Initial?> = exchanging(initial, kClazz) { field -> body(field).run { yielding { this } } }

    override fun <Initial : Any, Return> exchanging(
        initial: Initial?,
        kClazz: KClass<Initial>,
        body: YieldingScope<Field<ID, Initial?>, Field<ID, Return>>,
    ): Field<ID, Return> {
        val path: Path = stack.currentPath()
        val messages = messageProvider.messageAt<Initial>(path)
        val previous = stateAt(path, initial)
        val subject = newField(previous, messages)
        val context = YieldingContext<Field<ID, Initial?>, Field<ID, Return>>()
        return body(context, subject)
            .also {
                val message =
                    SingleOutboundMessage(
                        it.toSend.localValue,
                        when (it.toSend) {
                            is ConstantField<*, *> -> emptyMap()
                            else -> it.toSend.excludeSelf()
                        },
                    )
                toBeSent.addMessage(path, message)
                state += path to it.toSend.localValue
            }.toReturn
    }

    override fun <Initial, Return> evolving(
        initial: Initial,
        transform: YieldingScope<Initial, Return>,
    ): Return {
        val path = stack.currentPath()
        return transform(YieldingContext(), stateAt(path, initial))
            .also {
                check(it.toReturn !is Field<*, *>) {
                    "evolving operations cannot return fields (guaranteed misalignment on every neighborhood change)"
                }
                state += path to it.toSend
            }.toReturn
    }

    override fun <Scalar> neighboring(local: Scalar, kClazz: KClass<*>): Field<ID, Scalar> {
        if (messageProvider.requiresSerialization) {
            ListAnySerializer.registerType(kClazz.qualifiedName(local), kClazz)
        }
        val path = stack.currentPath()
        val neighborValues = messageProvider.messageAt<Scalar>(path)
        toBeSent.addMessage(path, SingleOutboundMessage(local))
        return newField(local, neighborValues)
    }

    override fun <Initial> evolve(
        initial: Initial,
        transform: (Initial) -> Initial,
    ): Initial =
        evolving(initial) {
            val res = transform(it)
            YieldingResult(res, res)
        }

    override fun <R> alignedOn(
        pivot: Any?,
        body: () -> R,
    ): R {
        stack.alignRaw(pivot)
        return body().also {
            stack.dealign()
        }
    }

    override fun align(pivot: Any?) = stack.alignRaw(pivot)

    override fun dealign() = stack.dealign()

    private fun <T> stateAt(
        path: Path,
        default: T,
    ): T = previousState.getTyped(path, default)
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
    val others = neighboring(0.toByte())
    return when {
        field.neighborsCount == others.neighborsCount -> field
        field.neighborsCount > others.neighborsCount -> others.mapWithId { id, _ -> field[id] }
        else ->
            error(
                """
                Collektive is in an inconsistent state, this is most likely a bug in the implementation.
                Field $field with ${field.neighborsCount} neighbors has been projected into a context
                with more neighbors, ${others.neighborsCount}: ${others.excludeSelf().keys}.
                """.trimIndent().replace(Regex("'\\R"), " "),
            )
    }
}

expect fun KClass<*>.qualifiedName(evidence: Any?): String
