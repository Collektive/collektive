package it.unibo.collektive.field

import it.unibo.collektive.ID

/**
 * A field is a map of messages where the key is the [ID] of a node and [T] the associated value.
 * @param T the type of the field.
 */
interface Field<out T> : Map<ID, T> {
    /**
     * The [ID] of the local node.
     */
    val localId: ID

    /**
     * The value associated with the [localId].
     */
    val local: T?

    /**
     * Exclude the local node from the field.
     */
    fun excludeSelf(): Field<T> = Field(localId, filter { it.key != localId })

    /**
     * Function for generic manipulation of the field.
     */
    fun <B> mapField(transform: (ID, T) -> B): Field<B> = Field(localId, mapValues { (id, value) -> transform(id, value) })

    /**
     * Transform the field into a map.
     */
    fun toMap(): Map<ID, T> = this

    companion object {
        /**
         * Build a field from a [localId] and a map of messages.
         */
        operator fun <T> invoke(localId: ID, messages: Map<ID, T> = emptyMap()): Field<T> = FieldImpl(localId, messages)
    }
}

/**
 * Transform a map into a field with a [localId].
 */
fun <T> Map<ID, T>.toField(localId: ID): Field<T> = Field(localId, this)

internal data class FieldImpl<T>(
    override val localId: ID,
    private val messages: Map<ID, T>,
) : Field<T>, Map<ID, T> by messages {
    override val local: T? = this[localId]
}
