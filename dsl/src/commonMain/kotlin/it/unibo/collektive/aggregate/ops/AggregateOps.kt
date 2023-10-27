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
 * val double: (Int) -> Int = { it * 2 }
 * val field = neighbouring(double(1))
 * ```
 * In this case, the field returned has the result of the computation as local value.
 */
fun <Return> AggregateContext.neighbouring(type: Return): Field<Return> {
    val body: (Field<Return>) -> Field<Return> = { f -> f.mapField { _, x -> x } } // imho non va bene
    return exchange(type, body)
}

/**
 * [share] captures the space-time nature of field computation through observation of neighbours’ values, starting from an [initial] value,
 * it reduces to a single local value given a [transform] function and updating and sharing to neighbours of a local variable.
 * ```
 * val a = share(0) {
 *   it.min()?.value!!
 * }
 * ```
 * ```
 * val b = share(0) {
 *   val minValue = it.min()?.value!!
 *   minValue butReturn "Something different"
 * }
 * ```
 *
 * ## N.B.:
 * Do not write code after calling the sending or returning values, they must be written at last inside the lambda.
 * ```
 * val dont = share(0){
 *  val minValue = it.min()?.value!!
 *  minValue butReturn "Dont do this"
 *  minValue
 * }
 * ```
 */
@Suppress("UNCHECKED_CAST")
fun <Initial, Return> AggregateContext.share(initial: Initial, transform: SharingContext<Initial, Return>.(Field<Initial>) -> Return): Return {
    val context = SharingContext<Initial, Return>()
    val local = exchange(initial) {
        it.mapField { _, _ ->
            val ts = transform(context, it)
            if (!context.areSameType) context.toBeSent else ts as Initial
        }
    }.local

    return if (context.areSameType) local as Return else context.toBeReturned as Return
}
