class Aggregate {
    // nbr
    fun <X : Any> neighbouring(event: X): FieldImpl<Any> =
        Environment.fields.retrieveField(event) ?:
        throw IllegalArgumentException("No field found of the selected event")
    // rep
    fun <X> repeating(initial: X, repeat: (X) -> X): X  = TODO()
    // share
    /*fun sharing() = println("sharing")*/
}

fun aggregate(init: Aggregate.() -> Unit): Aggregate = Aggregate().apply(init)
