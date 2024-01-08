package it.unibo.collektive.aggregate.api.impl.stack

import it.unibo.collektive.path.Path
import it.unibo.collektive.path.impl.PathImpl

internal class StackDequeue<X> : Stack<X> {
    private val currentStack = ArrayDeque<X?>()

    override fun currentPath(): Path = PathImpl(currentStack.toList())

    override fun alignRaw(token: X?) = currentStack.addLast(token)

    override fun dealign() {
        currentStack.removeLast()
    }

    override fun toString(): String = currentStack.toString()
}
