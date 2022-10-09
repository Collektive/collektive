class Fields<X : Any> {
    private val fields: MutableMap<Int, FieldImpl<X>> = mutableMapOf()

    fun retrieveAllFields(): List<FieldImpl<X>> {
        println(fields.toString())
        return fields.values.toList()
    }

    fun addField(event: X): FieldImpl<X>? = fields.put(event::class.hashCode(), FieldImpl())

    fun retrieveField(event: X): FieldImpl<X>? = fields[event::class.hashCode()]

    fun isFieldPresent(event: X): Boolean = fields.containsKey(event::class.hashCode())
}
