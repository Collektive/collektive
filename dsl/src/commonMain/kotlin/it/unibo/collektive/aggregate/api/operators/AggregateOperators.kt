package it.unibo.collektive.aggregate.api.operators

import arrow.core.Option
import arrow.core.getOrElse
import arrow.core.identity
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
 * val field = neighboring(0)
 * ```
 *
 * The field returned has as local value the value passed as input (0 in this example).
 *
 * ```kotlin
 * val field = neighboring({ 2 * 2 })
 * ```
 *
 * In this case, the field returned has the result of the computation as local value.
 */
fun <ID : Any, Scalar> Aggregate<ID>.neighboringViaExchange(local: Scalar): Field<ID, Scalar> =
    exchange(local, ::identity)

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
 * Do not write code after calling the sending or returning values,
 * they must be the last statement of the body (the lambda expression).
 *
 * ```
 * share(0) {
 *  val maxValue = it.maxBy { v -> v.value }.value
 *  maxValue.yielding { "Don't do this" }
 *  maxValue
 * }
 * ```
 */
fun <ID : Any, Initial, Return> Aggregate<ID>.sharing(
    initial: Initial,
    transform: YieldingContext<Initial, Return>.(Field<ID, Initial>) -> YieldingContext.YieldingResult<Initial, Return>,
): Return {
    val context = YieldingContext<Initial, Return>()
    var yieldingContext: Option<YieldingContext.YieldingResult<Initial, Return>> = none()
    exchange(initial) { initialField ->
        initialField.map { _ ->
            transform(context, initialField).also { context ->
                yieldingContext = context.some()
            }.toSend
        }
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
fun <ID : Any, Initial> Aggregate<ID>.share(initial: Initial, transform: (Field<ID, Initial>) -> Initial): Initial =
    sharing(initial) { field -> transform(field).run { yielding { this } } }
