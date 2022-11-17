package stack

import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Stack {
    private var currentStack: MutableList<String> = mutableListOf()
    fun clearStack() = currentStack.clear()
    fun currentPath(): Path = Path(currentStack.toList())
    fun addToken(token: String) {
        currentStack.add(token)
    }
}

data class Path(val path: List<String>)
