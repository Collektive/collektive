interface Field<out T>

class FieldImpl<T> : Field<T> {
    val field: Map<Int, T> = emptyMap()
    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}