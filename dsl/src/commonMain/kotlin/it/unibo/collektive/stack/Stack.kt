package it.unibo.collektive.stack

interface Stack<X> {
    fun currentPath(): Path
    fun alignRaw(token: X?)
    fun dealign()

    companion object {
        operator fun <X> invoke(): Stack<X> = StackImplDequeue()
    }
}

internal class StackImplDequeue<X> : Stack<X> {
    private val currentStack = ArrayDeque<X?>()

    override fun currentPath(): Path = Path(currentStack.toList())

    override fun alignRaw(token: X?) {
        currentStack.addLast(token)
    }

    override fun dealign() {
        currentStack.removeLast()
    }

    override fun toString(): String = currentStack.toString()
}

data class Path(val path: List<Any?>)
