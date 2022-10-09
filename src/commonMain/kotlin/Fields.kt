class Fields<X : Any> {
    private val fields: MutableMap<X, FieldImpl<X>> = mutableMapOf()

    fun retrieveAllFields(): List<FieldImpl<X>> = fields.values.toList()

    fun addField(event: X): FieldImpl<X>? = fields.put(event, FieldImpl())

    fun retrieveField(event: X): FieldImpl<X>? = fields[event]

    fun isFieldPresent(event: X): Boolean = fields.containsKey(event)

}