interface Path {
    fun addToken(token: Any?)
    fun closeToken(token: Any?)
    fun currentList(): List<String>
    fun current(): String
}

class PathImpl : Path {
    private val currentPath: MutableList<String> = mutableListOf()
    override fun addToken(token: Any?) {
        currentPath.add("{ $token")
    }

    override fun closeToken(token: Any?) {
        currentPath.add("$token }")
    }

    override fun currentList(): List<String> = currentPath.toList()

    override fun current(): String = currentPath.toList().toString()
}
