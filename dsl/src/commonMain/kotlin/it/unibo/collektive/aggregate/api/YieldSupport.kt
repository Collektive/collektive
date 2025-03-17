package it.unibo.collektive.aggregate.api

/**
 * Context for yielding operations (exchanging, sharing).
 * Yielding operations means operate on an [Initial] value (usually exchanged with neighbors),
 * but return a possibly different value [Return] to the caller.
 */
class YieldingContext<Initial, Return> {
    /**
     * Computes [toReturn] after the data exchange operation is complete.
     * ## Example
     * ```
     * sharing(0) { // Sent to neighbors: kotlin.Int
     *     it.max(Int.MIN_VALUE).yielding { it.toString() }
     * }
     * result // result: kotlin.String
     * ```
     * ```
     * val result = sharing(0) {
     *     val max = it.max(Int.MIN_VALUE)
     *     max.yielding { max.toString().takeIf { max > 1 } }
     * }
     * result // result: kotlin.String?
     * ```
     * Calling [yielding] in the body effectively performs the information exchange with neighbors,
     * preparing the local [Initial] value (from the extension receiver) to be sent away,
     * but returns the value produced by [toReturn].
     */
    fun Initial.yielding(toReturn: () -> Return): YieldingResult<Initial, Return> = YieldingResult(this, toReturn())
}

/**
 * Specifies the value [toSend] and the value [toReturn] of a yielding operator.
 */
data class YieldingResult<Initial, Return>(val toSend: Initial, val toReturn: Return)
