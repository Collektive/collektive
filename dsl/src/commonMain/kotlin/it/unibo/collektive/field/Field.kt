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
     * Map the field using the [transform] function.
     */
    fun <B> mapWithId(transform: (ID, T) -> B): Field<B>

    /**
     * Map the field using the [transform] function.
     */
    fun <B> map(transform: (T) -> B): Field<B> = mapWithId { _, value -> transform(value) }

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
     * Returns a map representing the field.
     */
    fun toMap(): Map<ID, T>

    companion object {

        /**
         * Build a field from a [localId], [localValue] and [others] neighbours values.
         */
        internal operator fun <T> invoke(localId: ID, localValue: T, others: Map<ID, T> = emptyMap()): Field<T> =
            ArrayBasedField(localId, localValue, others.map { it.toPair() })

        /**
         * Reduce the elements of the field using the [transform] function.
         */
        fun <T> Field<T>.reduce(transform: (accumulator: T, T) -> T): T =
            excludeSelf().values.reduce(transform)

        /**
         * Reduce the elements of a field starting with a [initial] value and a [transform] function.
         */
        fun <T, R> Field<T>.hoodInto(initial: R, transform: (R, T) -> R): R {
            var accumulator = initial
            for ((_, value) in excludeSelf()) {
                accumulator = transform(accumulator, value)
            }
            return accumulator
        }

        /**
         * Reduce the elements of a field using the [transform] function.
         */
        fun <T> Field<T>.hood(transform: (T, T) -> T): T = hoodInto(localValue, transform)
    }
}

internal data class ArrayBasedField<T>(
    override val localId: ID,
    override val localValue: T,
    private val others: List<Pair<ID, T>>,
) : Field<T> {

    override fun excludeSelf(): Map<ID, T> = others.toMap()

    override fun asSequence(): Sequence<Pair<ID, T>> = others.asSequence() + (localId to localValue)

    override fun toMap(): Map<ID, T> = (others + (localId to localValue)).toMap()

    override fun get(id: ID): T = when {
        id == localId -> localValue
        else -> others.first { it.first == id }.second
    }

    override fun <B> mapWithId(transform: (ID, T) -> B): Field<B> {
        val mappedValues = others.map { (id, value) -> id to transform(id, value) }
        return SequenceBasedField(localId, transform(localId, localValue), mappedValues.asSequence())
    }
}

internal data class SequenceBasedField<T>(
    override val localId: ID,
    override val localValue: T,
    private val others: Sequence<Pair<ID, T>>,
) : Field<T> by ArrayBasedField(localId, localValue, others.toList()) {

    override fun <B> mapWithId(transform: (ID, T) -> B): Field<B> {
        val mappedValues = others.map { (id, value) -> id to transform(id, value) }
        return SequenceBasedField(localId, transform(localId, localValue), mappedValues)
    }
}
