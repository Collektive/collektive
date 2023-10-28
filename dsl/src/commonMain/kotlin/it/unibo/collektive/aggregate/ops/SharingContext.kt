package it.unibo.collektive.aggregate.ops

/**
 * The lambda context passed to the [share] function.
 */
class SharingContext<Initial, Return> {
    internal var toBeSent: Initial? = null
    internal var toBeReturned: Return? = null
    internal var areSameType = true

    /**
     * Express the value [toReturn] when the [share] computation is done.
     * Usually the [toReturn] value is different from the [toSend],
     * otherwise use [share] without calling this function to return the value [toSend].
     * It can be used with checks after the invocation.
     * ## Example
     * ```
     * share(0) {
     *   sendButReturn(it.toMap().maxBy { v -> v.value }.value, "A string")
     * }
     * ```
     */
    fun sendButReturn(toSend: Initial, toReturn: Return): Return {
        toBeSent = toSend
        toBeReturned = toReturn
        areSameType = false
        return toReturn
    }

    /**
     * When the [share] computation is done, it evaluates the lambda [toReturn] over [toSend] value and returns its result.
     * ## Example
     * ```
     * share(0) {
     *   sendButReturn(it.toMap().minBy { v -> v.value }.value) { s ->
     *      if (s > 1) "Hello" else null
     *   }
     * }
     * ```
     */
    fun sendButReturn(toSend: Initial, toReturn: (Initial) -> Return): Return =
        sendButReturn(toSend, toReturn(toSend))
}
