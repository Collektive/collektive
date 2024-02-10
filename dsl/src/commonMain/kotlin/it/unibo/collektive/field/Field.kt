package it.unibo.collektive.field

import arrow.core.fold

/**
 * A field is a map of messages where the key is the [ID] of a node and [T] the associated value.
 */
sealed interface Field<ID : Any, out T> {
    /**
     * The [ID] of the local node.
     */
    val localId: ID

    /**
     * The value associated with the [localId].
     */
    val localValue: T

    /**
     * Returns a [Map] with the neighboring values of this field (namely, all values but self).
     */
    fun excludeSelf(): Map<ID, T>

    /**
     * Combines this field with another (aligned) one.
     */
    fun <B, R> alignedMap(other: Field<ID, B>, transform: (T, B) -> R): Field<ID, R> {
        checkAligned(this, other)
        return mapWithId { id, value -> transform(value, other[id] ?: error("Unintercepted misalignment")) }
    }

    /**
     * Map the field using the [transform] function.
     */
    fun <B> mapWithId(transform: (ID, T) -> B): Field<ID, B>

    /**
     * Map the field using the [transform] function.
     */
    fun <B> map(transform: (T) -> B): Field<ID, B> = mapWithId { _, value -> transform(value) }

    /**
     * Map the field resulting in a new one where the value for the local and the neighbors is [singleton].
     */
    fun <B> mapToConstantField(singleton: B): Field<ID, B> = ConstantField(localId, singleton, excludeSelf().keys)

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
         * Check if two fields are aligned, throws an IllegalStateException otherwise.
         */
        fun checkAligned(field1: Field<*, *>, field2: Field<*, *>) {
            val ids1: Set<Any?> = field1.toMap().keys
            val ids2: Set<Any?> = field2.toMap().keys
            check(ids1 == ids2) {
                """
                Alignment issue between $field1 and $field2, the different ids are: ${ids1 - ids2 + (ids2 - ids1)}
                This is most likely caused by a bug in Collektive, please report at
                https://github.com/Collektive/collektive/issues/new/choose
                """.trimIndent()
            }
        }

        /**
         * Build a field from a [localId], [localValue] and [others] neighbours values.
         */
        internal operator fun <ID : Any, T> invoke(
            localId: ID,
            localValue: T,
            others: Map<ID, T> = emptyMap(),
        ): Field<ID, T> = ArrayBasedField(localId, localValue, others.map { it.toPair() })

        /**
         * Reduce the elements of the field using the [transform] function.
         * The local value is not considered, unless explicitly passed as [default].
         */
        fun <ID : Any, T> Field<ID, T>.hood(default: T, transform: (T, T) -> T): T {
            val neighbors = excludeSelf()
            return when {
                neighbors.isEmpty() -> default
                else -> neighbors.values.reduce(transform)
            }
        }

        /**
         * Folds the elements of a field starting with an [initial] through a [transform] function.
         * The local value is not considered, unless explicitly passed as [initial].
         */
        fun <ID : Any, T, R> Field<ID, T>.fold(initial: R, transform: (R, T) -> R): R =
            excludeSelf().fold(initial) { accumulator, entry -> transform(accumulator, entry.value) }
    }
}

internal abstract class AbstractField<ID : Any, T>(
    override val localId: ID,
    override val localValue: T,
) : Field<ID, T> {

    private val asMap: Map<ID, T> by lazy {
        val result: Map<ID, T> = neighborsMap() + (localId to localValue)
        result
    }
    private val neighborhood: Map<ID, T> by lazy { neighborsMap() }

    final override fun toMap(): Map<ID, T> = asMap

    final override fun excludeSelf(): Map<ID, T> = neighborhood

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is Field<*, *> -> toMap() == other.toMap()
            else -> false
        }
    }

    final override fun hashCode(): Int = toMap().hashCode()

    final override operator fun get(id: ID): T = when {
        id == localId -> localValue
        else -> neighborValueOf(id)
    }

    final override fun <B> mapWithId(transform: (ID, T) -> B): Field<ID, B> {
        return SequenceBasedField(localId, transform(localId, localValue), mapOthersAsSequence(transform))
    }

    protected abstract fun neighborsMap(): Map<ID, T>

    protected abstract fun neighborValueOf(id: ID): T

    protected abstract fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>>

    final override fun toString() = toMap().toString()
}

internal class ArrayBasedField<ID : Any, T>(
    localId: ID,
    localValue: T,
    private val others: List<Pair<ID, T>>,
) : AbstractField<ID, T>(localId, localValue) {

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

internal class SequenceBasedField<ID : Any, T>(
    localId: ID,
    localValue: T,
    private val others: Sequence<Pair<ID, T>>,
) : AbstractField<ID, T>(localId, localValue) {

    override val neighborsCount by lazy { others.count() }

    override fun asSequence(): Sequence<Pair<ID, T>> = others + (localId to localValue)

    override fun neighborsMap(): Map<ID, T> = others.toMap()

    override fun neighborValueOf(id: ID): T = excludeSelf().getValue(id)

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        others.map { (id, value) -> id to transform(id, value) }
}

internal class ConstantField<ID : Any, T>(
    localId: ID,
    localValue: T,
    private val neighborsIds: Set<ID>,
) : AbstractField<ID, T>(localId, localValue) {
    override val neighborsCount: Int = neighborsIds.size

    private val reified by lazy {
        reifiedList.toMap()
    }

    private val reifiedList by lazy {
        neighborsIds.map { id -> id to localValue }.toList()
    }

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        reifiedList.asSequence().map { (id, _) -> id to transform(id, localValue) }

    override fun neighborValueOf(id: ID): T = localValue

    override fun neighborsMap(): Map<ID, T> = reified

    override fun asSequence(): Sequence<Pair<ID, T>> = reifiedList.asSequence() + (localId to localValue)
}
