package it.unibo.collektive

import it.unibo.collektive.field.Field

/**
 * Observes the value of an expression [type] across neighbours.
 */
fun <X> AggregateContext.neighbouring(type: X): Field<X> {
    val body: (Field<X>) -> Field<X> = { f -> f.mapField { _, x -> x } } // imho non va bene
    return exchange(type, body)
}

/**
 * TODO.
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

/**
 * TODO.
 */
class SharingContext<Initial, Return> {
    internal var toBeSent: Initial? = null
    internal var toBeReturned: Return? = null
    internal var areSameType = true

    /**
     * TODO .
     */
    infix fun Initial.butReturn(toReturn: Return): Return {
        toBeSent = this
        toBeReturned = toReturn
        areSameType = false
        return toReturn
    }
}
