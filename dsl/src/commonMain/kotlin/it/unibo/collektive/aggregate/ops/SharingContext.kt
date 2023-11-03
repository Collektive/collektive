package it.unibo.collektive.aggregate.ops

/**
 * The lambda context passed to the [share] function.
 */
class SharingContext<Initial, Return> {

    /**
     * Express the lambda [toReturn] when the [sharing] computation is done.
     * Usually the [toReturn] value is different from the [toSend],
     * otherwise use [share] without calling this function to return the value [toSend].
     * It can be used with checks after the invocation.
     * ## Example
     * ```
     * val result = sharing(0) {
     *   val maxValue = it.maxBy { v -> v.value }.value
     *   maxValue.yielding { "A string" }
     * }
     * result // result: Kotlin.String
     * ```
     * ```
     * val result = sharing(0) {
     *   val max = it.maxBy { v -> v.value }.value
     *   max.yielding { "Hello".takeIf { min > 1 } }
     * }
     * result // result: Kotlin.String?
     * ```
     * The invoke of [yielding] as the last statement of the body of the [share],
     * sent to the neighbours the [toSend] value, but returns from the [share] the [toReturn] value.
     */
    fun Initial.yielding(toReturn: () -> Return): SharingResult<Initial, Return> =
        SharingResult(this, toReturn())
}

/**
 * Specifies the value [toSend] and the value [toReturn] of a [SharingContext.yielding] function.
 */
data class SharingResult<Initial, Return>(val toSend: Initial, val toReturn: Return)
