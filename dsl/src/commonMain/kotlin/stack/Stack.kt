package stack

class Stack<X> {
    private val currentStack: MutableList<X?> = mutableListOf()
    fun currentPath(): Path = Path(currentStack.toList())
    fun alignRaw(token: X?){
        currentStack.add(token)
    }
    fun dealign(){
        currentStack.removeLast()
    }

    override fun toString(): String {
        return currentStack.toString()
    }
}

data class Path(val path: List<Any?>)
