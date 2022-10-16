import event.Event

sealed class Fields<X : Any> {
    abstract val fields: MutableMap<String, Field<X>>

    fun retrieveAllFields(): List<Field<X>> = fields.values.toList()

    fun addField(event: Event<X>): Field<X>? {
        return fields.put(event.identifier, FieldImpl())
    }

    fun retrieveField(event: Event<X>): Field<X> = fields[event.identifier]
            ?: throw IllegalArgumentException("No field found of the selected event")

    fun isFieldPresent(event: Event<X>): Boolean = fields.containsKey(event.identifier)
}

data class LocalFields<X : Any>(override val fields: MutableMap<String, Field<X>> = mutableMapOf()) : Fields<X>()
data class GlobalFields<X : Any>(override val fields: MutableMap<String, Field<X>> = mutableMapOf()) : Fields<X>()
