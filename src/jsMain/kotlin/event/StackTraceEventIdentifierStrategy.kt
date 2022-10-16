package event

actual class StackTraceEventIdentifierStrategy<X : Any> {
    actual fun generateIdentifier(subject: Event<X>): String {
        try {
            throw Error()
        } catch(e: Error) {
            val stackTrace = e.stackTraceToString()
            console.log(stackTrace)
            return stackTrace
        }
    }
}