package it.unibo.collektive.aggregate.ops

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.none
import arrow.core.some
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
    val body: (Field<Return>) -> Field<Return> = { f -> f.map { _, x -> x } }
    return exchange(type, body)
}

/**
 * [sharing] captures the space-time nature of field computation through observation of neighbours' values, starting
 * from an [initial] value, it reduces to a single local value given a [transform] function and updating and sharing
 * to neighbours of a local variable.
 * ```
 * val result = sharing(0) {
 *   val maxValue = it.maxBy { v -> v.value }.value
 *   maxValue.yielding { "Something different" }
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
 *  val maxValue = it.maxBy { v -> v.value }.value
 *  maxValue.yielding { "Don't do this" }
 *  maxValue
 * }
 * ```
 */
fun <Initial, Return> AggregateContext.sharing(
    initial: Initial,
    transform: SharingContext<Initial, Return>.(Field<Initial>) -> SharingResult<Initial, Return>,
): Return {
    val context = SharingContext<Initial, Return>()
    var res: Option<SharingResult<Initial, Return>> = none()
    exchange(initial) {
        it.map { _, _ -> transform(context, it).also { r -> res = r.some() }.toSend }
    }
    return res.getOrElse { error("This error should never be thrown") }.toReturn
}

/**
 * [share] captures the space-time nature of field computation through observation of neighbours' values, starting
 * from an [initial] value, it reduces to a single local value given a [transform] function and updating and sharing to
 * neighbours of a local variable.
 * ```
 * val result = share(0) {
 *   it.maxBy { v -> v.value }.value
 * }
 * result // result: kotlin.Int
 * ```
 * In the example above, the function [share] wil return a value that is the max found in the field.
 **/
fun <Initial> AggregateContext.share(initial: Initial, transform: (Field<Initial>) -> Initial): Initial =
    sharing(initial) {
        val res = transform(it)
        SharingResult(res, res)
    }
