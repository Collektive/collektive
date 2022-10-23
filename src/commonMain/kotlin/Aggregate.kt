import Environment.deviceId
import Environment.localFields
import event.EventImpl

class Aggregate {
    // nbr
    fun <X : Any> neighbouring(type: X): Field<Any> = localFields.retrieveField(EventImpl(type))

    // rep
    inline fun <reified X : Any, Y : Any> repeating(initial: X, noinline repeat: (X) -> Y): Y {
        val event = EventImpl(repeat)
        return if (localFields.isFieldPresent(event)) {
            val value = localFields.retrieveField(event).getById(deviceId)
            if (value is X){
                val result = repeat(value)
                localFields.retrieveField(event).addElement(deviceId, result)
                result
            } else {
                throw IllegalArgumentException("Wrong field found")
            }
        } else {
            val result = repeat(initial)
            localFields.addField(event)
            localFields.retrieveField(event).addElement(deviceId, result)
            result
        }
    }
    // share
    /*fun sharing() = println("sharing")*/
}

fun aggregate(init: Aggregate.() -> Unit): Aggregate = Aggregate().apply(init)
