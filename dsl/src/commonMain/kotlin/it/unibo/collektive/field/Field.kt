/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.field

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
     * Returns the number of neighbors in the field.
     */
    val neighborsCount: Int get() = excludeSelf().size

    /**
     * Returns the [ID]s of all neighbors in this field.
     */
    val neighbors: Collection<ID>

    /**
     * Returns a [Map] with the neighboring values of this field (namely, all values but self).
     */
    fun excludeSelf(): Map<ID, T>

    /**
     * Combines this field with another (aligned) one considering the [ID] when combining the values.
     */
    fun <B, R> alignedMapWithId(other: Field<ID, B>, transform: (ID, T, B) -> R): Field<ID, R> {
        checkAligned(this, other)
        return mapWithId { id, value -> transform(id, value, other[id]) }
    }

    /**
     * Combines this field with another (aligned) one.
     */
    fun <B, R> alignedMap(other: Field<ID, B>, transform: (T, B) -> R): Field<ID, R> =
        alignedMapWithId(other) { _, value, otherValue -> transform(value, otherValue) }

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
     * Transform the field into a sequence of pairs containing the [ID] and the associated value.
     */
    fun asSequence(): Sequence<Pair<ID, T>>

    /**
     * Converts the Field into a [Map].
     * This method is meant to bridge the aggregate APIs with the Kotlin collections framework.
     * The resulting map _will contain the local value_.
     */
    fun toMap(): Map<ID, T>

    /**
     * Base operations on [Field]s.
     */
    companion object {
        /**
         * Check if two or more fields are aligned, throwing an IllegalStateException otherwise.
         */
        fun checkAligned(field1: Field<*, *>, field2: Field<*, *>, vararg fields: Field<*, *>) {
            val ids: Collection<Any?> = field1.neighbors
            sequenceOf(field2, *fields).map { it.neighbors }.forEach {
                check(it.size == ids.size && it.containsAll(ids)) {
                    """
                    |Alignment issue among fields:
                    | - ${listOf(field1, field2, *fields).joinToString(separator = "\n| - ")}
                    |the different ids are: ${ids - it.toSet() + (it - ids.toSet())}
                    |This is most likely caused by a bug in Collektive, please report at
                    |https://github.com/Collektive/collektive/issues/new/choose
                    """.trimMargin()
                }
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
         * Reduce the elements of the field using the [reduce] function,
         * returning the [default] value if the field to transform is empty.
         * The local value is not considered.
         */
        inline fun <ID : Any, T> Field<ID, T>.hood(default: T, crossinline reduce: (T, T) -> T): T =
            hoodWithId(default) { (_, accumulator), (id, value) -> id to reduce(accumulator, value) }

        /**
         * Reduce the elements of the field using the [reduce] function,
         * returning the [default] value if the field to transform is empty.
         * The local value is not considered.
         *
         * The [reduce] function takes two pairs: the former represents the accumulated value (including the [ID]),
         * while the latter represents the current entry of the neighboring field that should be combined.
         *
         * Use this function when the [ID] should be propagated during the reduce operation.
         */
        inline fun <ID : Any, T> Field<ID, T>.hoodWithId(
            default: T,
            crossinline reduce: (Pair<ID, T>, Pair<ID, T>) -> Pair<ID, T>,
        ): T = hoodWithId(default, reduce) { second }

        /**
         * Reduce the entries of the field using the [reduce] function,
         * and finally transforming them to a [R]esult using [select].
         *
         * The [default] value is returned if the field to transform is empty.
         * The local value is not considered.
         */
        inline fun <ID : Any, T, R> Field<ID, T>.hoodWithId(
            default: R,
            crossinline reduce: (Pair<ID, T>, Pair<ID, T>) -> Pair<ID, T>,
            crossinline select: Pair<ID, T>.() -> R,
        ): R = hoodWithId(default, ::Pair, reduce, select)

        /**
         * Reduce the elements of the field by transforming them to [I]intermediates
         * selecting the [I]ntermediates using the [reduce] function,
         * and finally transforming the [I]ntermediates to [R]esults using [select].
         *
         * The [default] value is returned if the field to transform is empty.
         * The local value is not considered.
         */
        inline fun <ID : Any, T, I, R> Field<ID, T>.hoodWithId(
            default: R,
            crossinline transform: (ID, T) -> I,
            crossinline reduce: (I, I) -> I,
            crossinline select: I.() -> R,
        ): R {
            val neighbors = excludeSelf()
            return when {
                neighbors.isEmpty() -> default
                else -> neighbors.entries.asSequence()
                    .map { (id, v) -> transform(id, v) }
                    .reduce { accumulator, value -> reduce(accumulator, value) }
                    .select()
            }
        }

        /**
         * Reduce the elements of the field using the [transform] function,
         * it includes the [ID] of the element whenever it should be considered in the [transform] function,
         * but the [ID] is not returned.
         * The local value of the field is not considered.
         * Returns the [default] if the field to transform is empty.
         */
        inline fun <ID : Any, T> Field<ID, T>.hoodWithId(default: T, crossinline transform: (T, ID, T) -> T): T =
            hoodWithId(default) { (_, accumulator), (id, value) -> id to transform(accumulator, id, value) }

        /**
         * Accumulates the elements of a field starting from an [initial] through a [transform] function.
         * The local value of the field is not considered.
         */
        inline fun <ID : Any, T, R> Field<ID, T>.fold(initial: R, crossinline transform: (R, T) -> R): R =
            foldWithId(initial) { accumulator, _, value -> transform(accumulator, value) }

        /**
         * Accumulates the elements of a field starting from an [initial] through a
         * [transform] function that includes the [ID] of the element.
         * The local value of the field is not considered.
         */
        inline fun <ID : Any, T, R> Field<ID, T>.foldWithId(initial: R, crossinline transform: (R, ID, T) -> R): R {
            var accumulator = initial
            for (entry in excludeSelf()) {
                accumulator = transform(accumulator, entry.key, entry.value)
            }
            return accumulator
        }
    }
}

internal abstract class AbstractField<ID : Any, T>(override val localId: ID, override val localValue: T) :
    Field<ID, T> {
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

    final override fun <B> mapWithId(transform: (ID, T) -> B): Field<ID, B> =
        SequenceBasedField(localId, transform(localId, localValue), mapOthersAsSequence(transform))

    protected abstract fun neighborsMap(): Map<ID, T>

    protected abstract fun neighborValueOf(id: ID): T

    protected abstract fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>>

    final override fun toString() = "ϕ(localId=$localId, localValue=$localValue, neighbors=${neighborsMap()})"
}

internal class ArrayBasedField<ID : Any, T>(localId: ID, localValue: T, private val others: List<Pair<ID, T>>) :
    AbstractField<ID, T>(localId, localValue) {
    override val neighborsCount: Int get() = others.size
    override val neighbors: Collection<ID> by lazy { others.map { it.first } }

    override fun neighborValueOf(id: ID): T = when {
        others.size <= MAP_OVER_LIST_PERFORMANCE_CROSSING_POINT -> others.first { it.first == id }.second
        else -> excludeSelf().getValue(id)
    }

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        others.asSequence().map { (id, value) -> id to transform(id, value) }

    override fun neighborsMap(): Map<ID, T> = others.toMap()

    override fun asSequence(): Sequence<Pair<ID, T>> = others.asSequence() + (localId to localValue)

    private companion object {
        const val MAP_OVER_LIST_PERFORMANCE_CROSSING_POINT = 16
    }
}

internal class SequenceBasedField<ID : Any, T>(localId: ID, localValue: T, private val others: Sequence<Pair<ID, T>>) :
    AbstractField<ID, T>(localId, localValue) {
    override val neighborsCount get() = neighbors.size

    override val neighbors: Collection<ID> by lazy { others.map { it.first }.toList() }

    override fun asSequence(): Sequence<Pair<ID, T>> = others + (localId to localValue)

    override fun neighborsMap(): Map<ID, T> = others.toMap()

    override fun neighborValueOf(id: ID): T = excludeSelf().getValue(id)

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        others.map { (id, value) -> id to transform(id, value) }
}

internal class ConstantField<ID : Any, T>(localId: ID, localValue: T, private val neighborsIds: Set<ID>) :
    AbstractField<ID, T>(localId, localValue) {
    override val neighborsCount: Int = neighborsIds.size

    override val neighbors: Collection<ID> = neighborsIds

    private val reified by lazy {
        reifiedList.toMap()
    }

    private val reifiedList by lazy {
        neighborsIds.map { id -> id to localValue }
    }

    override fun <R> mapOthersAsSequence(transform: (ID, T) -> R): Sequence<Pair<ID, R>> =
        reifiedList.asSequence().map { (id, _) -> id to transform(id, localValue) }

    override fun neighborValueOf(id: ID): T = localValue

    override fun neighborsMap(): Map<ID, T> = reified

    override fun asSequence(): Sequence<Pair<ID, T>> = reifiedList.asSequence() + (localId to localValue)
}
