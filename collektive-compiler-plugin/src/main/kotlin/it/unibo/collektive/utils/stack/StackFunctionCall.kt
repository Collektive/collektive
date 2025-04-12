package it.unibo.collektive.utils.stack

/**
 * A stack that keeps track of the function calls in the code.
 */
class StackFunctionCall {
    private val stack = ArrayDeque<String>()
    private val occurrences = mutableMapOf<String, Int>()

    /**
     * Pushes a new function call to the stack.
     */
    fun push(name: String) {
        val counter = occurrences[name]?.let { it + 1 } ?: 1
        occurrences[name] = counter
        val candidateToken = "$name.$counter"
        stack.addFirst(candidateToken)
    }

    /**
     * Pops the last function call from the stack.
     */
    fun pop(): String? = stack.removeFirstOrNull()

    override fun toString(): String = stack.joinToString(separator = ",")
}
