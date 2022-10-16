package event

interface Event<out X : Any> {
    val type: X
    val identifier: String
}

class EventImpl<X : Any>(override val type: X): Event<X> {
    override val identifier: String = StackTraceEventIdentifierStrategy<X>().generateIdentifier(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as EventImpl<*>
        if (type != other.type) return false
        return true
    }

    override fun hashCode(): Int {
        return type.hashCode()
    }

}
