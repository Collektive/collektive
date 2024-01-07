package it.unibo.collektive.aggregate.api

/**
 * Context for yielding operations.
 * Yielding operations means that an operator can send a [Initial] value to the neighbours,
 * but return a [Return] value to the caller.
 */
class YieldingContext<Initial, Return> {

    /**
     * Express the lambda [toReturn] when the computation is done.
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
     * The call of [yielding] as the last statement of the body of a yielding operator,
     * sent to the neighbours the [Initial] value (from the extension receiver),
     * but returns the [toReturn] value.
     */
    fun Initial.yielding(toReturn: () -> Return): YieldingResult<Initial, Return> =
        YieldingResult(this, toReturn())

    /**
     * Specifies the value [toSend] and the value [toReturn] of a yielding operator.
     */
    data class YieldingResult<Initial, Return>(val toSend: Initial, val toReturn: Return)
}
