sealed class Fields<X : Any> {
    abstract val fields: MutableMap<Int, Field<X>>

    fun retrieveAllFields(): List<Field<X>> = fields.values.toList()

    fun addField(event: X): Field<X>? = fields.put(event::class.hashCode(), FieldImpl())

    fun retrieveField(event: X): Field<X> = fields[event::class.hashCode()] ?:
        throw IllegalArgumentException("No field found of the selected event")

    fun isFieldPresent(event: X): Boolean = fields.containsKey(event::class.hashCode())
}

data class LocalFields<X : Any>(override val fields: MutableMap<Int, Field<X>> = mutableMapOf()) : Fields<X>()
data class GlobalFields<X : Any>(override val fields: MutableMap<Int, Field<X>> = mutableMapOf()) : Fields<X>()
