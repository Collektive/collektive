interface Field<ID, out T : Any> {
    val local: T
    fun toMap(): Map<ID, T>
    operator fun get(id: ID): T
    fun fieldSize(): Int
}

class FieldImpl<ID, out T : Any>(override val local: T) : Field<ID, T> {
    private val field: Map<ID, T> = emptyMap()
    override fun get(id: ID): T = field[id]
        ?: throw IllegalArgumentException("No value found of the selected event")
    override fun toMap(): Map<ID, T> = field.toMap()
    override fun fieldSize(): Int = field.size
    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}
