package it.unibo.collektive

import it.unibo.collektive.field.Field
import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.ReceivedMessage
import it.unibo.collektive.messages.SentMessage
import it.unibo.collektive.networking.Network
import it.unibo.collektive.stack.Path
import it.unibo.collektive.stack.Stack
import it.unibo.collektive.state.State

/**
 * Context for managing aggregate computation.
 * @param localId: id of the device.
 * @param messages: map with all the messages sent by the neighbors.
 * @param previousState: last device state.
 */
class AggregateContext(
    private val localId: ID,
    private val messages: Set<ReceivedMessage>,
    private val previousState: Set<State<*>>,
) {

    private val stack = Stack<Any>()
    private var state = setOf<State<Any?>>()
    private var toBeSent = setOf<SentMessage>()

    /**
     * Messages to send to the other nodes.
     */
    fun messagesToSend(): Set<SentMessage> = toBeSent

    /**
     * Return the current state of the device as a new state.
     */
    fun newState(): Set<State<Any?>> = state

    /**
     * This function computes the local value of e_i, substituting variable n with the nvalue w of
     * messages received from neighbors, using the local value of e_i as a default.
     * The exchange returns the neighboring or local value v_r from the evaluation of e_r.
     * e_s evaluates to a nvalue w_s consisting of local values to be sent to neighbor devices δ′,
     * which will use their corresponding w_s(δ') as soon as they wake up and perform their next execution round.
     *
     * Often, expressions e_r and e_s coincide, so this function provides a shorthand for exchange(e_i, (n) => (e, e)).
     *
     * @param initial The initial value of e_i.
     * @param body A lambda that defines the computation for exchange, with access to the `Field` subject.
     * @return The result of the exchange, typically the neighboring or local value v_r.
     */
    fun <X, Y> exchange(initial: X, body: (Field<X>) -> Field<Y>): Field<Y> {
        val messages = messagesAt<X>(stack.currentPath())
        val previous = stateAt<X>(stack.currentPath()) ?: initial
        val subject = Field(localId, messages + (localId to previous))
        return body(subject).also { field ->
            toBeSent = toBeSent + IsotropicMessage(localId, mapOf(stack.currentPath() to field.local))
            if (messages.isNotEmpty()) {
                field.toMap().filterNot { it.key == localId }.map { (id, value) ->
                    val old = toBeSent
                        .filterIsInstance<AnisotropicMessage>()
                        .firstOrNull { it.senderId == localId && it.receiverId == id }
                        ?: AnisotropicMessage(localId, id, mapOf(stack.currentPath() to value))
                    toBeSent = (
                        toBeSent.filterNot { m ->
                            m is AnisotropicMessage && m.senderId == localId && m.receiverId == id
                        } + AnisotropicMessage(localId, id, old.message + (stack.currentPath() to value))
                        ).toSet()
                }
            }
            state = state.filterNot { stack.currentPath() == it.path }.toSet() + State(stack.currentPath(), field.local)
        }
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

    @Suppress("UNCHECKED_CAST")
    private fun <T> messagesAt(path: Path): Map<ID, T> = messages
        .filter { it.messages.containsKey(path) }
        .associate { it.senderId to it.messages[path] as T }

    @Suppress("UNCHECKED_CAST")
    private fun <T> stateAt(path: Path): T? = previousState.firstOrNull { it.path == path }?.value as? T
}

/**
 * Aggregate program entry point which computes a single iteration, taking as parameters the previous state.
 * @param localId: id of the device.
 * @param messages: map with all the messages sent by the neighbors.
 * @param state: last device state.
 * @param init: lambda with AggregateContext object receiver that provides the aggregate constructs.
 */
fun <X> aggregate(
    localId: ID,
    messages: Set<ReceivedMessage> = emptySet(),
    state: Set<State<*>> = emptySet(),
    init: AggregateContext.() -> X,
) = singleCycle(localId, messages, state, compute = init)

/**
 * Aggregate program entry point which computes multiple iterations.
 * @param condition: lambda that establish the number of iterations.
 * @param network: data structure used to allow the local communication between devices.
 * @param init: lambda with AggregateContext object receiver that provides the aggregate constructs.
 */
fun <X> aggregate(
    localId: ID,
    condition: () -> Boolean,
    network: Network,
    init: AggregateContext.() -> X,
) = runUntil(localId, condition, network, compute = init)
