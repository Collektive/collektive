import Environment.deviceId
import Environment.localFields

class Aggregate {
    // nbr
    fun <X : Any> neighbouring(event: X): Field<Any> = localFields.retrieveField(event)

    // rep
    inline fun <reified X : Any> repeating(initial: X, noinline repeat: (X) -> X): X {
        return if (localFields.isFieldPresent(repeat)) {
            val value = localFields.retrieveField(repeat).getById(deviceId)
            if (value is X){
                val result = repeat(value)
                localFields.retrieveField(repeat).addElement(deviceId, result)
                result
            } else {
                throw IllegalArgumentException("Wrong field found")
            }
        } else {
            val result = repeat(initial)
            localFields.addField(repeat)
            localFields.retrieveField(repeat).addElement(deviceId, result)
            result
        }
    }
    // share
    /*fun sharing() = println("sharing")*/
}

fun aggregate(init: Aggregate.() -> Unit): Aggregate = Aggregate().apply(init)
