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
     *   val maxValue = it.maxBy { v -> v.value }.value
     *   maxValue yielding "A string"
     * }
     * result // result: Kotlin.String
     * ```
     * The invoke of [yielding] as the last statement of the body of the [share],
     * sent to the neighbours the [toSend] value, but returns from the [share] the [toReturn] value.
     */
    fun Initial.yielding(toReturn: Return): Return {
        toBeSent = this
        toBeReturned = toReturn
        areSameType = false
        return toReturn
    }

    /**
     * When the [share] computation is done, it evaluates the lambda [toReturn] over [toSend] value and returns its result.
     * ## Example
     * ```
     * val res: String? = share(0) {
     *   val max = it.minBy { v -> v.value }.value
     *   max yielding { if (max > 1) "Hello" else null }
     * }
     * ```
     */
    fun Initial.yielding(toReturn: () -> Return): Return =
        yielding(toReturn())
}
