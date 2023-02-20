package it.unibo.alchemist.model.api

import ID
import Network
import it.unibo.alchemist.model.interfaces.*
import it.unibo.alchemist.model.interfaces.Node.Companion.asPropertyOrNull
import stack.Path

class CollektiveDevice<P> @JvmOverloads constructor(
    private val environment: Environment<Any, P>,
    override val node: Node<Any>,
    private val retainMessagesFor: Time,
) : NodeProperty<Any>, ID, Network where P : Position<P> {

    private var validMessages = mapOf<ID, Pair<Time, Map<Path, *>>>()
    var currentTime: Time = Time.ZERO

    fun receiveMessage(time: Time, from: ID, message: Map<Path, *>) {
        validMessages += from to (time to message)
    }

    override fun cloneOnNewNode(node: Node<Any>) = TODO()

    override fun send(localId: ID, message: Map<Path, *>) {
        environment.getNeighborhood(node)
            .mapNotNull { it.asPropertyOrNull<Any, CollektiveDevice<P>>() }
            .forEach { it.receiveMessage(currentTime, localId, message) }
    }

    override fun receive(): Map<ID, Map<Path, *>> = validMessages
        .filter { (_, value) -> value.first + retainMessagesFor >= currentTime }
        .also { validMessages = it }
        .mapValues { (_, value) -> value.second }
}
