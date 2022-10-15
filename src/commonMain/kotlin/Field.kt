interface Field<T : Any> {
    fun addElement(id: Int, value: T): T?
    fun getById(id: Int): T
}

class FieldImpl<T : Any> : Field<T> {
    val field: MutableMap<Int, T> = mutableMapOf()
    override fun addElement(id: Int, value: T): T? = field.put(id, value)
    override fun getById(id: Int): T = field[id]
        ?: throw IllegalArgumentException("No value found of the selected event")
    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}