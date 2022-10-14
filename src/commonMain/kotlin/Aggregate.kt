class Aggregate {
    // nbr
    fun <X : Any> neighbouring(event: X): Field<Any> =
        Environment.fields.retrieveField(event) ?:
        throw IllegalArgumentException("No field found of the selected event")

    // rep
    inline fun <reified X : Any> repeating(initial: X, repeat: (X) -> X): X {
        return if (Environment.fields.isFieldPresent(initial)) {
            val value = Environment.fields.retrieveField(initial)?.getById(Environment.deviceId)
            if (value is X){
                repeat(value)
            } else {
                throw IllegalArgumentException("Wrong field found")
            }
        } else {
            repeat(initial)
        }
    }
    // share
    /*fun sharing() = println("sharing")*/
}

fun aggregate(init: Aggregate.() -> Unit): Aggregate = Aggregate().apply(init)
