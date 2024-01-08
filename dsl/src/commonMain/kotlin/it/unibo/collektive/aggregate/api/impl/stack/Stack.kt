package it.unibo.collektive.aggregate.api.impl.stack

import it.unibo.collektive.path.Path

/**
 * Simple Stack interface with additional methods for the aggregate computation.
 */
internal interface Stack<X> {
    /**
     * Returns the current path of the stack.
     */
    fun currentPath(): Path

    /**
     * Pushes the [token] in the stack.
     */
    fun alignRaw(token: X?)

    /**
     * Pops the last element of the stack.
     */
    fun dealign()

    companion object {
        /**
         * Smart constructor for the [Stack] interface.
         */
        operator fun <X> invoke(): Stack<X> = StackDequeue()
    }
}
