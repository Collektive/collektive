class Fields<X : Any> {
    private val fields: MutableMap<Int, Field<X>> = mutableMapOf()

    fun retrieveAllFields(): List<Field<X>> = fields.values.toList()

    fun addField(event: X): Field<X>? = fields.put(event::class.hashCode(), FieldImpl())

    fun retrieveField(event: X): Field<X>? = fields[event::class.hashCode()]

    fun isFieldPresent(event: X): Boolean = fields.containsKey(event::class.hashCode())
}
