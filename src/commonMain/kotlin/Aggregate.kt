class Aggregate {
    // nbr
    fun <X : Any> neighbouring(event: X) : FieldImpl<Any> = Environment.fields.retrieveField(event)
    // rep
    /*fun repeating() = println("repeating")
    // share
    fun sharing() = println("sharing")*/
}

fun aggregate(init: Aggregate.() -> Unit): Aggregate = Aggregate().apply(init)
