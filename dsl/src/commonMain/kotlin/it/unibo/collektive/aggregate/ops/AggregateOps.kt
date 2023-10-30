package it.unibo.collektive.aggregate.ops

import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.field.Field

/**
 * Observes the value of an expression [type] across neighbours.
 *
 * ## Example
 * ```
 * val field = neighbouring(0)
 * ```
 * The field returned has as local value the value passed as input (0 in this example).
 * ```
 * val field = neighbouring({ 2 * 2 })
 * ```
 * In this case, the field returned has the result of the computation as local value.
 */
fun <Return> AggregateContext.neighbouring(type: Return): Field<Return> {
    val body: (Field<Return>) -> Field<Return> = { f -> f.mapField { _, x -> x } }
    return exchange(type, body)
}

/**
 * [share] captures the space-time nature of field computation through observation of neighbours' values, starting from
 * an [initial] value, it reduces to a single local value given a [transform] function and updating and sharing to
 * neighbours of a local variable.
 * ```
 * val result = share(0) {
 *   it.toMap().maxBy { v -> v.value }.value
 * }
 * result // result: kotlin.Int
 * ```
 * In the example above, the function [share] wil return a value that is the max found in the field.
 * ```
 * val result = share(0) {
 *   sendButReturn(it.toMap().maxBy { v -> v.value }.value, "Something different")
 * }
 * result // result: kotlin.String
 * ```
 * In the example above, the function [share] wil return the string initialised as in [sendButReturn].
 *
 * ### Invalid use:
 *
 * Do not write code after calling the sending or returning values, they must be written at last inside the lambda.
 * ```
 * share(0) {
 *  val minValue = it.toMap().minBy { v -> v.value }.value
 *  sendButReturn(minValue, "Don't do this")
 *  minValue
 * }
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <Initial, Return> AggregateContext.share(
    initial: Initial,
    transform: SharingContext<Initial, Return>.(Field<Initial>) -> Return,
): Return {
    val context = SharingContext<Initial, Return>()
    val local = exchange(initial) {
        it.mapField { _, _ ->
            val ts = transform(context, it)
            if (!context.areSameType) context.toBeSent else ts as Initial
        }
    }.local

    return if (context.areSameType) local as Return else context.toBeReturned as Return
}
