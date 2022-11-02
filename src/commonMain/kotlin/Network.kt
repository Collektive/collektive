import stack.Path

interface Network {
    fun send(message: Map<Path, *>)
    fun receive(): Map<ID, Map<Path, *>>
}

class NetworkImpl(private val localId: ID) : Network {
    private val sentMessages: MutableMap<ID, Map<Path, *>> = mutableMapOf()

    override fun send(message: Map<Path, *>) {
        sentMessages[localId] = message
    }

    override fun receive(): Map<ID, Map<Path, *>> = sentMessages.toMap().also { sentMessages.clear() }
}
