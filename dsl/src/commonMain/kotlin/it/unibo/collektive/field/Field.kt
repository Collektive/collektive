package it.unibo.collektive.field

import it.unibo.collektive.ID
import it.unibo.collektive.aggregate.AggregateContext
import it.unibo.collektive.aggregate.ops.neighbouring

/**
 * A field is a map of messages where the key is the [ID] of a node and [T] the associated value.
 * @param T the type of the field.
 */
sealed interface Field<out T> {
    /**
     * The [ID] of the local node.
     */
    val localId: ID

    /**
     * The value associated with the [localId].
     */
    val localValue: T

    /**
     * Returns a map with the neighboring values of this field (namely, all values but self).
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
     * Converts the Field into a [Map].
     * This method is meant to bridge the aggregate APIs with the Kotlin collections framework.
     * The resulting map _will contain the local value_.
     */
    fun toMap(): Map<ID, T>

    /**
     * Returns the number of neighbors of the field.
     */
    val neighborsCount: Int get() = excludeSelf().size

    companion object {

        /**
         * Build a field from a [localId], [localValue] and [others] neighbours values.
         */
        internal operator fun <T> invoke(localId: ID, localValue: T, others: Map<ID, T> = emptyMap()): Field<T> =
            ArrayBasedField(localId, localValue, others.map { it.toPair() })

        /**
         * Reduce the elements of the field using the [transform] function.
         */
        fun <T> Field<T>.reduce(includingSelf: Boolean = true, transform: (accumulator: T, T) -> T): T =
            when (includingSelf) {
                true -> toMap().values.reduce(transform)
                false -> excludeSelf().values.reduce(transform)
            }

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

internal abstract class AbstractField<T>(
    override val localId: ID,
    override val localValue: T,
) : Field<T> {

    private val asMap: Map<ID, T> by lazy { neighborsMap() + (localId to localValue) }
    private val neighborhood: Map<ID, T> by lazy { neighborsMap() }

    final override fun toMap(): Map<ID, T> = asMap

    final override fun excludeSelf(): Map<ID, T> = neighborhood

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is Field<*> -> toMap() == other.toMap()
            else -> false
        }
    }

    final override fun hashCode(): Int = toMap().hashCode()

    final override operator fun get(id: ID): T = when {
        id == localId -> localValue
        else -> neighborValueOf(id)
    }

    final override fun <B> mapWithId(transform: (ID, T) -> B): Field<B> {
        return SequenceBasedField(localId, transform(localId, localValue), mapOthersAsSequence(transform))
    }

    protected abstract fun neighborsMap(): Map<ID, T>

    protected abstract fun neighborValueOf(id: ID): T

    protected abstract fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>>

    final override fun toString() = toMap().toString()
}

internal class ArrayBasedField<T>(
    localId: ID,
    localValue: T,
    private val others: List<Pair<ID, T>>,
) : AbstractField<T>(localId, localValue) {

    override val neighborsCount: Int get() = others.size

    override fun neighborValueOf(id: ID): T = when {
        others.size <= MAP_OVER_LIST_PERFORMANCE_CROSSING_POINT -> others.first { it.first == id }.second
        else -> excludeSelf().getValue(id)
    }

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        others.map { (id, value) -> id to transform(id, value) }.asSequence()

    override fun neighborsMap(): Map<ID, T> = others.toMap()

    override fun asSequence(): Sequence<Pair<ID, T>> = others.asSequence() + (localId to localValue)

    companion object {
        const val MAP_OVER_LIST_PERFORMANCE_CROSSING_POINT = 16
    }
}

internal class SequenceBasedField<T>(
    localId: ID,
    localValue: T,
    private val others: Sequence<Pair<ID, T>>,
) : AbstractField<T>(localId, localValue) {

    override val neighborsCount by lazy { others.count() }

    override fun asSequence(): Sequence<Pair<ID, T>> = others + (localId to localValue)

    override fun neighborsMap(): Map<ID, T> = others.toMap()

    override fun neighborValueOf(id: ID): T = excludeSelf().getValue(id)

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        others.map { (id, value) -> id to transform(id, value) }
}

/**
 * TODO.
 */
fun <T> AggregateContext.project(field: Field<T>): Field<T> {
    val others = neighbouring(0.toByte())
    return when {
        field.neighborsCount == others.neighborsCount -> field
        field.neighborsCount > others.neighborsCount -> others.mapWithId { id, _ -> field[id] }
        else -> error(
            """
                Collektive is in an inconsistent state, this is most likely a bug in the implementation.
                Field ${field} with ${field.neighborsCount} neighbors has been projected into a context
                with more neighbors, ${others.neighborsCount}: ${others.excludeSelf().keys}.
                """.trimIndent().replace(Regex("'\\R"), " ")
        )
    }
}
