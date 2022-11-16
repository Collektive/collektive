import stack.Path

interface Network {
    fun send(localId: ID, message: Map<Path, *>)
    fun receive(): Map<ID, Map<Path, *>>
}

class NetworkImpl : Network {
    private val sentMessages: MutableMap<ID, Map<Path, *>> = mutableMapOf()

    override fun send(localId: ID, message: Map<Path, *>) {
        sentMessages[localId] = message
    }

    override fun receive(): Map<ID, Map<Path, *>> = sentMessages.toMap()
}
