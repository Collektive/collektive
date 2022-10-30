package stack

interface Path {
    fun addToken(token: Any?)
    fun currentList(): List<String>
    fun current(): String
}

class PathImpl : Path {
    private val currentPath: MutableList<String> = mutableListOf()
    override fun addToken(token: Any?) {
        currentPath.add("$token")
    }

    override fun currentList(): List<String> = currentPath.toList()

    override fun current(): String = currentPath.toList().toString()
    override fun toString(): String {
        return "stack.PathImpl(currentPath=$currentPath)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as PathImpl

        if (currentPath != other.currentPath) return false

        return true
    }

    override fun hashCode(): Int {
        return currentPath.hashCode()
    }
}
