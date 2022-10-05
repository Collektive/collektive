interface Field<out T>

class FieldImpl<T> : Field<T> {
    val field: Map<Int, T> = emptyMap()
}