package it.unibo.collektive.aggregate.api

import it.unibo.collektive.field.Field

typealias YieldingScope<Initial, Return> = YieldingContext<Initial, Return>.(Initial) -> YieldingResult<Initial, Return>

/**
 * Models the minimal set of aggregate operations.
 * Holds the [localId] of the device executing the aggregate program.
 */
interface Aggregate<ID : Any> {
    /**
     * The local [ID] of the device.
     */
    val localId: ID

    /**
     * The [exchange] function manages the computation of values between neighbors in a specific context.
     * It computes a [body] function starting from the [initial] value and the messages received from other neighbors,
     * then sends the results from the evaluation to specific neighbors or to everyone,
     * it is contingent upon the origin of the calculated value, whether it was received from a neighbor or if it
     * constituted the initial value.
     *
     * ## Example
     * ```
     * exchange(0) { f ->
     *  f.mapField { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
     * }
     * ```
     * The result of the exchange function is a field with as messages a map with key the id of devices across the
     * network and the result of the computation passed as relative local values.
     */
    fun <Initial> exchange(
        initial: Initial,
        body: (Field<ID, Initial>) -> Field<ID, Initial>,
    ): Field<ID, Initial>

    /**
     * Same behavior of [exchange] but this function can yield a [Field] of [Return] value.
     *
     * ## Example
     * ```
     * exchanging(initial = 1) {
     *   val fieldResult = it + 1
     *   fieldResult.yielding { fieldResult.map { value -> "return: $value" } }
     * }
     * ```
     */
    fun <Initial, Return> exchanging(
        initial: Initial,
        body: YieldingScope<Field<ID, Initial>, Field<ID, Return>>,
    ): Field<ID, Return>

    /**
     * Iteratively updates the value computing the [transform] expression at each device using the last
     * computed value or the [initial].
     */
    fun <Initial> evolve(
        initial: Initial,
        transform: (Initial) -> Initial,
    ): Initial

    /**
     * Iteratively updates the value computing the [transform] expression from a [YieldingContext]
     * at each device using the last computed value or the [initial].
     */
    fun <Initial, Return> evolving(
        initial: Initial,
        transform: YieldingScope<Initial, Return>,
    ): Return

    /**
     * Observes the value of an expression [local] across neighbours.
     *
     * ## Example
     *
     * ```kotlin
     * val field = neighboring(0)
     * ```
     *
     * The field returned has as local value the value passed as input (0 in this example).
     *
     * ```kotlin
     * val field = neighboring({ 2 * 2 })
     * ```
     *
     * In this case, the field returned has the computation as a result,
     * in form of a field of functions with type `() -> Int`.
     */
    fun <Scalar> neighboring(local: Scalar): Field<ID, Scalar>

    /**
     * Alignment function that pushes in the stack the pivot, executes the body and pop the last
     * element of the stack after it is called.
     * Returns the body's return element.
     */
    fun <R> alignedOn(
        pivot: Any?,
        body: () -> R,
    ): R

    /**
     * Pushes the pivot in the alignment stack.
     */
    fun align(pivot: Any?)

    /**
     * Pops the last element of the alignment stack.
     */
    fun dealign()
}
