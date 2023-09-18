package it.unibo.collektive.stack

/**
 * Simple Stack interface with additional methods for the aggregate computation.
 */
interface Stack<X> {
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
        operator fun <X> invoke(): Stack<X> = StackImplDequeue()
    }
}

internal class StackImplDequeue<X> : Stack<X> {
    private val currentStack = ArrayDeque<X?>()

    override fun currentPath(): Path = Path(currentStack.toList())

    override fun alignRaw(token: X?) = currentStack.addLast(token)

    override fun dealign() {
        currentStack.removeLast()
    }

    override fun toString(): String = currentStack.toString()
}

/**
 * A [path] is a list of tokens that represents the current position in the aggregate computation.
 */
data class Path(val path: List<Any?>)
