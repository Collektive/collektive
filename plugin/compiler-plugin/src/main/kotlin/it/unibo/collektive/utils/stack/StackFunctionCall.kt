package it.unibo.collektive.utils.stack

class StackFunctionCall {
    private val stack = ArrayDeque<String>()
    private val occurrences = mutableMapOf<String, Int>()

    fun push(name: String) {
        val counter = occurrences[name]?.let { it + 1 } ?: 1
        occurrences[name] = counter
        val candidateToken = "$name.$counter"
        stack.addFirst(candidateToken)
    }

    fun pop(): String? = stack.removeFirstOrNull()

    override fun toString(): String = stack.toString()
}