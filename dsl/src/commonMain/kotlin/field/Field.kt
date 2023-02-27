package field

import ID

interface Field<T> {
    val local: Pair<ID, *>
    fun toMap(): Map<ID, T>
    operator fun get(id: ID): T
}

@Suppress("UNCHECKED_CAST")
class FieldImpl<T>(override val local: Pair<ID, *>, messages: Map<ID, *> = emptyMap<ID, T>()) : Field<T> {
    private val field: Map<ID, T> =  (mapOf(local) + messages) as Map<ID, T>
    override fun get(id: ID): T = field[id]
        ?: throw IllegalArgumentException("No value found for the specified id")

    override fun toMap(): Map<ID, T> = field.toMap()
    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}
