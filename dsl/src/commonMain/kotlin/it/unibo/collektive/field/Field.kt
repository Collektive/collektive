package it.unibo.collektive.field

import it.unibo.collektive.ID

interface Field<out T> : Map<ID, T> {
    val localId: ID
    val local: T?
    fun excludeSelf(): Field<T> = Field(localId, filter { it.key != localId })
    fun <B> map(transform: (T) -> B): Field<B> = Field(localId, this.mapValues { (_, value) -> transform(value) })

    companion object {
        operator fun <T> invoke(localId: ID, messages: Map<ID, T> = emptyMap()): Field<T> = FieldImpl(localId, messages)
    }
}

internal data class FieldImpl<T>(
    override val localId: ID,
    private val messages: Map<ID, T>,
) : Field<T>, Map<ID, T> by messages { override val local: T? = this[localId] }
