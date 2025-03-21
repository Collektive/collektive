package it.unibo.collektive.aggregate.api.impl.stack

import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathFactory

internal class StackDeque(private val pathFactory: PathFactory) : Stack {
    private val currentStack = ArrayDeque<Any?>()

    override fun currentPath(): Path = pathFactory(currentStack.toList())

    override fun alignRaw(token: Any?) = currentStack.addLast(token)

    override fun dealign() {
        currentStack.removeLast()
    }

    override fun toString(): String = currentStack.toString()
}
