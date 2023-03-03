package field

import ID

interface Field<out T> {
    val localId: ID
    val messages: Map<ID, *>
    fun toMap(): Map<ID, T>
    fun getSelf(): Map<ID, T>
    fun excludeSelf(): Map<ID, T>
    operator fun get(id: ID): T
}

@Suppress("UNCHECKED_CAST")
class FieldImpl<T>(override val localId: ID, override val messages: Map<ID, *> = emptyMap<ID, T>()) : Field<T> {

    private val field: Map<ID, T> = messages as Map<ID, T>
    override fun get(id: ID): T = field[id]
        ?: throw IllegalArgumentException("No value found for the specified id")

    override fun toMap(): Map<ID, T> = field.toMap()
    override fun getSelf(): Map<ID, T> = messages.filterKeys { it == localId } as Map<ID, T>

    override fun excludeSelf(): Map<ID, T> = messages.filterKeys { it != localId } as Map<ID, T>

    override fun toString(): String {
        return "FieldImpl(field=$field)"
    }
}
