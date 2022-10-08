interface Field<out T>

class FieldImpl<T> : Field<T> {
    val field: MutableMap<Int, T> = mutableMapOf()

    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}