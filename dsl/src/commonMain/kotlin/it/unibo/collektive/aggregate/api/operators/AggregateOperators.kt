package it.unibo.collektive.aggregate.api.operators

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.none
import arrow.core.some
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.YieldingContext
import it.unibo.collektive.field.Field

/**
 * Observes the value of an expression [local] across neighbours.
 *
 * ## Example
 *
 * ```kotlin
 * val field = neighbouring(0)
 * ```
 *
 * The field returned has as local value the value passed as input (0 in this example).
 *
 * ```kotlin
 * val field = neighbouring({ 2 * 2 })
 * ```
 *
 * In this case, the field returned has the result of the computation as local value.
 */
fun <Scalar> Aggregate.neighboring(local: Scalar): Field<Scalar> = exchange(local) { it.mapWithId { _, x -> x } }

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
 *
 * In the example above, the function [sharing] will return the string initialised as in yielding.
 *
 * ### Invalid use:
 *
 * Do not write code after calling the sending or returning values, they must be written at last inside the lambda.
 *
 * ```
 * share(0) {
 *  val maxValue = it.maxBy { v -> v.value }.value
 *  maxValue.yielding { "Don't do this" }
 *  maxValue
 * }
 * ```
 */
fun <Initial, Return> Aggregate.sharing(
    initial: Initial,
    transform: YieldingContext<Initial, Return>.(Field<Initial>) -> YieldingContext.YieldingResult<Initial, Return>,
): Return {
    val context = YieldingContext<Initial, Return>()
    var yieldingContext: Option<YieldingContext.YieldingResult<Initial, Return>> = none()
    exchange(initial) {
        it.mapWithId { _, _ -> transform(context, it).also { context -> yieldingContext = context.some() }.toSend }
    }
    return yieldingContext.getOrElse { error("This error should never be thrown") }.toReturn
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
fun <Initial> Aggregate.share(initial: Initial, transform: (Field<Initial>) -> Initial): Initial =
    sharing(initial) { field -> transform(field).let { YieldingContext.YieldingResult(it, it) } }
