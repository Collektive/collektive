interface Field<ID, out T> {
    val local: Pair<ID, T>
    fun toMap(): Map<ID, T>
    operator fun get(id: ID): T
}

class FieldImpl<ID, out T>(override val local: Pair<ID, T>, messages: Map<ID, T> = emptyMap()) : Field<ID, T> {
    private val field: Map<ID, T> =  mapOf(local) + messages
    override fun get(id: ID): T = field[id]
        ?: throw IllegalArgumentException("No value found for the specified id")
    override fun toMap(): Map<ID, T> = field.toMap()
    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}
