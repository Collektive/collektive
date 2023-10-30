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
     * val result = share(0) {
     *   sendButReturn(it.toMap().maxBy { v -> v.value }.value, "A string")
     * }
     * result // result: Kotlin.String
     * ```
     * The invoke of [sendButReturn] as the last statement of the body of the [share],
     * sent to the neighbours the [toSend] value, but returns from the [share] the [toReturn] value.
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
     * val res: String? = share(0) {
     *   val min = it.toMap().minBy { v -> v.value }.value
     *   sendButReturn(min) {
     *      if (min > 1) "Hello" else null
     *   }
     * }
     * ```
     */
    fun sendButReturn(toSend: Initial, toReturn: () -> Return): Return =
        sendButReturn(toSend, toReturn())
}
