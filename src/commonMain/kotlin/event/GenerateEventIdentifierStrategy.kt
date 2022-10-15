package event

class HashCodeEventIdentifierStrategy<X : Any> {
    fun generateIdentifier(subject: Event<X>): Int = subject.hashCode()
}