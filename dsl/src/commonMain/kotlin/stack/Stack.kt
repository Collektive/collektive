package stack

class Stack<X> {
    private var currentStack: MutableList<X> = mutableListOf()
    fun clearStack() = currentStack.clear()
    fun currentPath(): Path = Path(currentStack.toList())
    fun alignRaw(token: X){
        currentStack.add(token)
    }
    fun dealing(){
        currentStack.removeLast()
    }
}

data class Path(val path: List<Any?>)
