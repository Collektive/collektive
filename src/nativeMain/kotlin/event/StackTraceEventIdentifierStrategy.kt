package event

actual class StackTraceEventIdentifierStrategy<X : Any> {
    actual fun generateIdentifier(subject: Event<X>): String {
        try {
            throw Exception()
        } catch (e: Exception){
            val stackTrace = e.stackTraceToString()
            println(stackTrace)
            return stackTrace
        }
    }
}