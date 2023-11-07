package it.unibo.collektive.field

import it.unibo.collektive.ID

/**
 * A field is a map of messages where the key is the [ID] of a node and [T] the associated value.
 * @param T the type of the field.
 */
interface Field<out T> {
    /**
     * The [ID] of the local node.
     */
    val localId: ID

    /**
     * The value associated with the [localId].
     */
    val localValue: T

    /**
     * Exclude the local node from the field.
     */
    fun excludeSelf(): Map<ID, T>

    /**
     * Function for generic manipulation of the field.
     */
    fun <B> map(transform: (ID, T) -> B): Field<B>

    /**
     * Get the value associated with the [id].
     * Raise an error if the [id] is not present in the field.
     */
    operator fun get(id: ID): T

    /**
     * Transform the field into a map.
     */
    fun asSequence(): Sequence<Pair<ID, T>>

    /**
     * Returns a map repesenting the field.
     */
    fun toMap(): Map<ID, T>

    companion object {
        /**
         * Build a field from a [localId] and a list of messages.
         */
        internal operator fun <T> invoke(
            localId: ID,
            localValue: T,
            others: List<Pair<ID, T>> = emptyList(),
        ): Field<T> = FieldImpl(localId, localValue, others)

        /**
         * Build a field from a [localId], [localValue] and [others] neighbours values.
         */
        internal operator fun <T> invoke(localId: ID, localValue: T, others: Map<ID, T> = emptyMap()): Field<T> =
            FieldImpl(localId, localValue, others.map { it.toPair() })

        /**
         * Reduce the elements of the field using the [transform] function.
         */
        fun <T> Field<T>.reduce(transform: (accumulator: T, T) -> T): T =
            excludeSelf().values.reduce(transform)

        /**
         * Reduce the elements of a field starting with a [initial] value and a [transform] function.
         */
        fun <T> Field<T>.hoodInto(initial: T = localValue, transform: (T, T) -> T): T {
            var accumulator = initial
            for ((_, value) in excludeSelf()) {
                accumulator = transform(accumulator, value)
            }
            return accumulator
        }

        /**
         * Reduce the elements of a field starting with a [initial] value and a [transform] function.
         */
        fun <T, R> Field<T>.hood(initial: R, transform: (R, T) -> R): R {
            var accumulator = initial
            for ((_, value) in asSequence()) {
                accumulator = transform(accumulator, value)
            }
            return accumulator
        }
    }
}

internal data class FieldImpl<T>(
    override val localId: ID,
    override val localValue: T,
    private val others: List<Pair<ID, T>>,
) : Field<T> {
    private val otherClosedToMap by lazy { others.toMap() }

    override fun excludeSelf(): Map<ID, T> = others.toMap()
    override fun get(id: ID): T {
        return if (id == localId) localValue else otherClosedToMap[id] ?: error("Field not aligned")
    }

    override fun toMap(): Map<ID, T> = otherClosedToMap + (localId to localValue)

    override fun asSequence(): Sequence<Pair<ID, T>> = others.asSequence() + (localId to localValue)

    override fun <B> map(transform: (ID, T) -> B): Field<B> {
        val mappedValues = others.map { (id, value) -> id to transform(id, value) }
        val mappedLocalValue = transform(localId, localValue)
        return FieldImpl(localId, mappedLocalValue, mappedValues)
    }
}
