/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

/**
 * A field is a map of messages where the key is the [ID] of a node and [T] the associated value.
 */
sealed interface Field<ID : Any, out T> {
    /**
     * The [ID] of the local node.
     */
    @Deprecated("Use local.id instead", replaceWith = ReplaceWith("local.id"))
    val localId: ID

    /**
     * The value associated with the [localId].
     */
    @Deprecated("Use local.value instead", replaceWith = ReplaceWith("local.value"))
    val localValue: T

    /**
     * The [FieldEntry] of the local node.
     */
    val local: FieldEntry<ID, T>

    /**
     * Returns the number of neighbors in the field.
     */
    val neighborsCount: Int get() = excludeSelf().size

    /**
     * Returns the [ID]s of all neighbors in this field.
     */
    val neighbors: Set<ID>

    /**
     * Returns the values of all neighbors in this field.
     */
    val neighborsValues: List<T> // get() = excludeSelf().values

    /**
     * Returns a [Map] with the neighboring values of this field (namely, all values but self).
     */
    fun excludeSelf(): Map<ID, T>

    /**
     * Combines this field with another (aligned) one considering the [ID] when combining the values.
     */
    fun <B, R> alignedMap(other: Field<ID, B>, transform: (ID, T, B) -> R): Field<ID, R> {
        // checkAligned(this, other)
        return map { transform(it.id, it.value, other[it.id]) }
    }

    /**
     * Combines this field with other two (aligned) fields based on the [ID]s when.
     */
    fun <B, C, R> alignedMap(f1: Field<ID, B>, f2: Field<ID, C>, transform: (ID, T, B, C) -> R): Field<ID, R> {
        // checkAligned(this, f1, f2)
        return map { (id, value) -> transform(id, value, f1[id], f2[id]) }
    }

    /**
     * Combines this field with another (aligned) one.
     */
    fun <B, R> alignedMapValues(other: Field<ID, B>, transform: (T, B) -> R): Field<ID, R> =
        alignedMap(other) { _, value, otherValue -> transform(value, otherValue) }

    /**
     * Combines this field with other two (aligned) fields based on the [ID]s when.
     */
    fun <B, C, R> alignedMapValues(f1: Field<ID, B>, f2: Field<ID, C>, transform: (T, B, C) -> R): Field<ID, R> =
        alignedMap(f1, f2) { _, value, f1Value, f2Value -> transform(value, f1Value, f2Value) }

    /**
     * Checks if a specified entry is contained within the current field.
     *
     * @param entry The FieldEntry object to check for existence in the current field.
     * @return `true` if the entry is equal to [local] or exists in the neighborhood; `false` otherwise.
     */
    operator fun contains(entry: FieldEntry<ID, *>): Boolean =
        entry == local || entry in excludeSelf().entries.asSequence().map { it.toFieldEntry() }

    /**
     * Checks if the given ID is present in the field, including [local].
     *
     * @param id the identifier to check for presence
     * @return `true` if the ID matches the local ID or is found in the neighbors, `false` otherwise
     */
    fun containsId(id: ID): Boolean = local.id == id || id in neighbors

    /**
     * Map the field using the [transform] function.
     */
    fun <B> map(transform: (FieldEntry<ID, T>) -> B): Field<ID, B>

    /**
     * Map the field using the [transform] function.
     */
    fun <B> mapValues(transform: (T) -> B): Field<ID, B> = map { (_, value) -> transform(value) }

    /**
     * Map the field resulting in a new one where the value for the local and the neighbors is [singleton].
     */
    fun <B> mapToConstant(singleton: B): Field<ID, B> = ConstantField(local.id, singleton, excludeSelf().keys)

    /**
     * Get the value associated with the [id].
     * Raise an error if the [id] is not present in the field.
     */
    operator fun get(id: ID): T

    /**
     * Transform the field into a sequence of pairs containing the [ID] and the associated value.
     */
    fun asSequence(): Sequence<FieldEntry<ID, T>>

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
        ): Field<ID, T> {
            check(localId !in others) {
                "A field cannot be constructed with local id '$localId', " +
                    "local value '$localValue'," +
                    "and neighborhood values '$others' " +
                    "as the local id is also present among the neighbors"
            }
            return when {
                others.isEmpty() -> PointwiseField(localId, localValue)
                others.values.all { it == localValue } -> ConstantField(localId, localValue, others.keys)
                else -> ArrayBasedField(localId, localValue, others.map { it.toFieldEntry() })
            }
        }

        /**
         * Reduce the elements of the field using the [reduce] function,
         * returning the [default] value if the field to transform is empty.
         * The local value is not considered.
         */
        @Deprecated("Use the standard library version")
        @Suppress("DEPRECATION")
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
        @Deprecated("Use the standard library version")
        @Suppress("DEPRECATION")
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
        @Deprecated("Use the standard library version")
        @Suppress("DEPRECATION")
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
        @Deprecated("Use the standard library version")
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
        @Suppress("DEPRECATION")
        @Deprecated("Use the standard library version")
        inline fun <ID : Any, T> Field<ID, T>.hoodWithId(default: T, crossinline transform: (T, ID, T) -> T): T =
            hoodWithId(default) { (_, accumulator), (id, value) -> id to transform(accumulator, id, value) }

        /**
         * Accumulates the elements of a field starting from an [initial] through a [transform] function.
         * The local value of the field is not considered.
         */
        @Suppress("DEPRECATION")
        @Deprecated(
            "Use the standard library version",
            replaceWith = ReplaceWith(
                "this.foldValues(initial, transform)",
                "it.unibo.collektive.stdlib.fields.foldValues",
            ),
        )
        inline fun <ID : Any, T, R> Field<ID, T>.fold(initial: R, crossinline transform: (R, T) -> R): R =
            foldWithId(initial) { accumulator, _, value -> transform(accumulator, value) }

        /**
         * Accumulates the elements of a field starting from an [initial] through a
         * [transform] function that includes the [ID] of the element.
         * The local value of the field is not considered.
         */
        @Deprecated("Use the standard library version")
        inline fun <ID : Any, T, R> Field<ID, T>.foldWithId(initial: R, crossinline transform: (R, ID, T) -> R): R {
            var accumulator = initial
            for (entry in excludeSelf()) {
                accumulator = transform(accumulator, entry.key, entry.value)
            }
            return accumulator
        }
    }
}

internal abstract class AbstractField<ID : Any, T>(override val local: FieldEntry<ID, T>) : Field<ID, T> {

    @Deprecated("Use local.id instead", replaceWith = ReplaceWith("local.id"))
    override val localId: ID get() = local.id

    @Deprecated("Use local.value instead", replaceWith = ReplaceWith("local.value"))
    override val localValue: T get() = local.value

    constructor(localID: ID, localValue: T) : this(FieldEntry(localID, localValue))

    private val asMap: Map<ID, T> by lazy {
        val result: Map<ID, T> = neighborsMap() + (local.id to local.value)
        result
    }
    private val neighborhood: Map<ID, T> by lazy { neighborsMap() }

    private val stringRepresentation: String by lazy {
        val neighborsList = excludeSelf().toList()
        val sortedEntries = neighborsList.sortedWith { (id1, v1), (id2, v2) ->
            when (val byId = tryCompare(id1, id2)) {
                0 -> tryCompare(v1, v2)
                else -> byId
            }
        }
        val sortedString = sortedEntries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (id, value) ->
            "$id=$value"
        }
        "ϕ(localId=${local.id}, localValue=${local.value}, neighbors=$sortedString)"
    }

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
        id == local.id -> local.value
        else -> neighborValueOf(id)
    }

    final override fun <B> map(transform: (FieldEntry<ID, T>) -> B): Field<ID, B> =
        SequenceBasedField(local.id, transform(local), mapOthersAsSequence(transform))

    protected abstract fun neighborsMap(): Map<ID, T>

    protected abstract fun neighborValueOf(id: ID): T

    protected abstract fun <R> mapOthersAsSequence(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>>

    private fun <T> tryCompare(a: T, b: T): Int = when {
        a is Comparable<*> && b is Comparable<*> -> {
            runCatching {
                @Suppress("UNCHECKED_CAST")
                (a as Comparable<T>).compareTo(b)
            }.getOrDefault(0)
        }
        else -> 0
    }

    final override fun toString() = stringRepresentation
}

internal class ArrayBasedField<ID : Any, T>(localId: ID, localValue: T, private val others: List<FieldEntry<ID, T>>) :
    AbstractField<ID, T>(localId, localValue) {

    override val neighborsCount: Int get() = others.size
    override val neighbors: Set<ID> by lazy {
        others.mapTo(mutableSetOf()) {
            checkNotLocal(it.id)
            it.id
        }
    }
    override val neighborsValues by lazy { others.mapTo(ArrayList(neighborsCount)) { it.value } }

    override fun neighborValueOf(id: ID): T = when {
        others.size <= MAP_OVER_LIST_PERFORMANCE_CROSSING_POINT -> others.first { it.id == id }.value
        else -> excludeSelf().getValue(id)
    }

    override fun <R> mapOthersAsSequence(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> =
        others.asSequence().map {
            checkNotLocal(it.id)
            it.map(transform)
        }

    override fun neighborsMap(): Map<ID, T> = buildMap {
        others.forEach { this[checkNotLocal(it.id)] = it.value }
    }

    override fun asSequence(): Sequence<FieldEntry<ID, T>> = others.asSequence() + local

    private companion object {
        const val MAP_OVER_LIST_PERFORMANCE_CROSSING_POINT = 16
    }
}

internal class SequenceBasedField<ID : Any, T>(
    localId: ID,
    localValue: T,
    private val others: Sequence<FieldEntry<ID, T>>,
) : AbstractField<ID, T>(localId, localValue) {

    override val neighborsCount get() = neighbors.size

    override val neighbors: Set<ID> by lazy {
        others.mapTo(mutableSetOf()) { checkNotLocal(it.id) }
    }

    override val neighborsValues: List<T> by lazy {
        others.mapTo(ArrayList(neighborsCount)) {
            checkNotLocal(it.id)
            it.value
        }
    }

    override fun asSequence(): Sequence<FieldEntry<ID, T>> = others + local

    override fun neighborsMap(): Map<ID, T> = buildMap {
        putAll(
            others.map {
                checkNotLocal(it.id)
                it.pair
            },
        )
    }

    override fun neighborValueOf(id: ID): T = excludeSelf().getValue(id)

    override fun <R> mapOthersAsSequence(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> =
        others.map {
            checkNotLocal(it.id)
            it.map(transform)
        }
}

internal class ConstantField<ID : Any, T>(localId: ID, localValue: T, override val neighbors: Set<ID>) :
    AbstractField<ID, T>(localId, localValue) {
    override val neighborsCount: Int = neighbors.size

    override val neighborsValues: List<T> by lazy {
        buildList(neighbors.size) {
            repeat(neighbors.size) {
                add(localValue)
            }
        }
    }

    private val reified by lazy {
        reifiedList.toMap()
    }

    private val reifiedList by lazy {
        neighbors.map { id -> checkNotLocal(id) to localValue }
    }

    override fun <R> mapOthersAsSequence(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> =
        neighbors.asSequence().map { FieldEntry(checkNotLocal(it), local.value).map(transform) }

    override fun neighborValueOf(id: ID): T = local.value

    override fun neighborsMap(): Map<ID, T> = reified

    override fun asSequence(): Sequence<FieldEntry<ID, T>> =
        neighbors.asSequence().map { FieldEntry(checkNotLocal(it), local.value) } + local
}

internal class PointwiseField<ID : Any, T>(localId: ID, localValue: T) : AbstractField<ID, T>(localId, localValue) {

    override fun neighborsMap(): Map<ID, T> = emptyMap()

    override fun neighborValueOf(id: ID): T = local.value.also {
        check(id == local.id) {
            "A pointwise field $this was required to provide a value for id $id"
        }
    }

    override fun <R> mapOthersAsSequence(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> =
        emptySequence()

    override val neighbors: Set<ID> get() = emptySet()
    override val neighborsValues: List<T> get() = emptyList()

    override fun asSequence(): Sequence<FieldEntry<ID, T>> = sequenceOf(FieldEntry(local.id, local.value))
}

private fun <ID : Any> Field<ID, *>.checkNotLocal(id: ID): ID {
    check(id != local.id) {
        "Local value $id is both local and neighboring. This is a bug in ${this::class.simpleName}"
    }
    return id
}
