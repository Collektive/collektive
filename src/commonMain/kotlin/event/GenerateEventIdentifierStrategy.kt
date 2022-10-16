package event

expect class StackTraceEventIdentifierStrategy<X : Any>() {
    fun generateIdentifier(subject: Event<X>): String
}