/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:CollektiveIgnore("The implementation of field must not be internally projected")

package it.unibo.collektive.aggregate

import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.CollektiveIgnore

/**
 * A [Field] represents the local view of distributed values across a network of nodes.
 *
 * Each field consists of:
 * - A local entry, associated with the node executing the aggregate computation.
 * - A set of neighboring entries, each identified by a unique [ID].
 *
 * The field provides functional operators for aggregation, projection, and transformation,
 * supporting key aggregate computing patterns. Fields are expected to be aligned when
 * used together - i.e., they share the same neighborhood structure.
 *
 * @param ID the type used to identify nodes in the aggregate space.
 * @param T the type of value carried by each entry in the field.
 */
@Suppress("TooManyFunctions")
sealed interface Field<ID : Any, out T> {

    /**
     * A collapsing view over the field entries that includes the local entry and all neighbors.
     * Provides access to the combined list/set/sequence of self + peers, e.g., for operations that
     * need to reason about the entire neighborhood including the local node.
     */
    val all: CollapseAll<FieldEntry<ID, T>>

    /**
     * The [Aggregate] execution context this field belongs to.
     */
    val context: Aggregate<ID>

    /**
     * The entry representing the local node in the field.
     */
    val default: T

    /**
     * A collapsing view over the field entries that excludes the local entry, i.e., only peers.
     * Provides access to the neighborhood without the local node, for computations scoped to neighbors.
     */
    val neighbors: CollapseNeighbors<FieldEntry<ID, T>>

    /**
     * The entry representing the local node in the field.
     */
    val local: FieldEntry<ID, T>

    /**
     * Map the field using the [transform] function.
     * A `null` [ID] is used to represent the [default] value of the field.
     */
    fun <B> map(transform: (FieldEntry<ID?, T>) -> B): Field<ID, B>

    /**
     * Map the field resulting in a new one where the value for the local and the neighbors is [singleton].
     */
    fun <B> mapToConstant(singleton: B): Field<ID, B> =
        ConstantField(context, local.id, singleton, neighbors.ids.sequence)

    /**
     * Get the value associated with the [id].
     * Raise an error if the [id] is not present in the field.
     */
    operator fun get(id: ID?): T

    /**
     * Base operations on [Field]s.
     */
    companion object Fields {

        /**
         * Check if two or more fields are aligned, throwing an IllegalStateException otherwise.
         */
        fun <ID : Any> checkAligned(field1: Field<ID, *>, field2: Field<ID, *>, vararg fields: Field<ID, *>) {
            val ids: Set<ID> = field1.neighbors.ids.set
            sequenceOf(field2, *fields).map { it.neighbors.ids.set }.forEach {
                check(it.size == ids.size && it.containsAll(ids)) {
                    """
                    |Alignment issue among fields:
                    | - ${listOf(field1, field2, *fields).joinToString(separator = "\n| - ")}
                    |the different ids are: ${ids - it + (it - ids)}
                    |This is most likely caused by a bug in Collektive, please report at
                    |https://github.com/Collektive/collektive/issues/new/choose
                    """.trimMargin()
                }
            }
        }

        private fun noFieldsInFields(id: Any, value: Any?) = check(value !is Field<*, *>) {
            "Fields cannot contain other fields as values. The provided local value at id '$id' is: '$value'."
        }

        /**
         * Build a field from a [localId], [localValue] and [others] neighbours values.
         */
        internal operator fun <ID : Any, T> invoke(
            context: Aggregate<ID>,
            default: T,
            localId: ID,
            localValue: T = default,
            others: Map<ID, T> = emptyMap(),
        ): Field<ID, T> {
            others.forEach { (id, value) ->
                check(localId != id) {
                    "A field cannot be constructed with local id '$localId', " +
                        "local value '$localValue'," +
                        "and neighborhood values '$others' " +
                        "as the local id is also present among the neighbors"
                }
                noFieldsInFields(id, value)
            }
            noFieldsInFields(localId, localValue)
            return when {
                others.isEmpty() -> PointwiseField(context, localId, default, localValue)
                others.values.all { it == localValue } ->
                    ConstantField(context, localId, localValue, others.keys.asSequence())
                else -> ArrayBasedField(context, default, localId, localValue, others.map { it.toFieldEntry() })
            }
        }
    }
}

internal abstract class AbstractField<ID : Any, T>(
    final override val context: Aggregate<ID>,
    final override val default: T,
    final override val local: FieldEntry<ID, T>,
) : Field<ID, T> {

    constructor(
        context: Aggregate<ID>,
        default: T,
        localID: ID,
        localValue: T = default,
    ) : this(context, default, FieldEntry(localID, localValue))

    protected abstract val asMap: Map<ID, T>

    protected val sortedEntries by lazy {
        neighbors.sequence.sortedWith { (id1, v1), (id2, v2) ->
            when (val byId = tryCompare(id1, id2)) {
                0 -> tryCompare(v1, v2)
                else -> byId
            }
        }
    }

    protected open val stringRepresentation: String by lazy {
        val sortedString = sortedEntries.joinToString(separator = ", ", prefix = "{", postfix = "}") { (id, value) ->
            "$id=$value"
        }
        "ϕ(default: $default, local: $local, neighbors: $sortedString)"
    }

    final override fun equals(other: Any?): Boolean {
        if (this === other) return true
        return when (other) {
            is AbstractField<*, *> -> asMap == other.asMap
            is Field<*, *> -> error("Field type '${other::class.simpleName}' is not supported by Collektive: $other")
            else -> false
        }
    }

    final override fun hashCode(): Int = asMap.hashCode()

    override operator fun get(id: ID?): T = if (id == null) default else asMap.getValue(id)

    final override fun <B> map(transform: (FieldEntry<ID?, T>) -> B): Field<ID, B> = SequenceBasedField(
        context,
        context.alignedOn(FieldDefaultKey) { transform(FieldEntry(null, default)) },
        local.id,
        context.alignedOn(local.id) { transform(local) },
        mapNeighbors(transform),
    )

    override fun <B> mapToConstant(singleton: B): Field<ID, B> =
        ConstantField(context, local.id, singleton, neighbors.ids.sequence)

    protected abstract fun <R> mapNeighbors(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>>

    final override fun toString() = stringRepresentation

    private object FieldDefaultKey

    private companion object {
        private fun <T> tryCompare(a: T, b: T): Int = when {
            a is Comparable<*> && b is Comparable<*> -> {
                runCatching {
                    @Suppress("UNCHECKED_CAST")
                    (a as Comparable<T>).compareTo(b)
                }.getOrDefault(0)
            }
            else -> 0
        }
    }
}

private class ArrayBasedField<ID : Any, T>(
    context: Aggregate<ID>,
    default: T,
    localId: ID,
    localValue: T,
    private val others: List<FieldEntry<ID, T>>,
) : AbstractField<ID, T>(context, default, localId, localValue) {

    override val asMap: Map<ID, T> by lazy {
        others.associateTo(LinkedHashMap(others.size + 1, 1.0f)) { it.pair }
            .apply { put(localId, localValue) }
    }

    override val neighbors: CollapseNeighbors<FieldEntry<ID, T>> get() = ListBackedCollapse(others)

    override val all: CollapseAll<FieldEntry<ID, T>>
        get() = SequenceBackedCollapse(others.asSequence() + local)

    override fun <R> mapNeighbors(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> =
        others.asSequence().map {
            checkNotLocal(it.id)
            context.alignedOn(it.id) { it.map(transform) }
        }
}

private class SequenceBasedField<ID : Any, T>(
    context: Aggregate<ID>,
    default: T,
    localId: ID,
    localValue: T,
    private val others: Sequence<FieldEntry<ID, T>>,
) : AbstractField<ID, T>(context, default, localId, localValue) {

    override val all: CollapseAll<FieldEntry<ID, T>> get() = SequenceBackedCollapse(others + local)

    override val neighbors: CollapseNeighbors<FieldEntry<ID, T>> get() = SequenceBackedCollapse(others)

    override val asMap: Map<ID, T> by lazy { (others + local).toMap() }

    override fun <R> mapNeighbors(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> = others.map {
        checkNotLocal(it.id)
        context.alignedOn(it.id) { it.map(transform) }
    }
}

internal class ConstantField<ID : Any, T>(
    context: Aggregate<ID>,
    localId: ID,
    localValue: T,
    val neighborsIDs: Sequence<ID>,
) : AbstractField<ID, T>(context, localValue, localId, localValue) {

    override val all: CollapseAll<FieldEntry<ID, T>> get() =
        SequenceBackedCollapse(neighborsAsSequence() + local)

    override val asMap: Map<ID, T> by lazy {
        (neighborsIDs + localId).associateWith { localValue }
    }

    override val neighbors: CollapseNeighbors<FieldEntry<ID, T>> get() =
        SequenceBackedCollapse(neighborsAsSequence())

    override val stringRepresentation: String by lazy {
        "ϕ(default: $default, constant: $localValue, localId: $localId, neighborsIDs: ${sortedEntries.map {
            it.id
        }.toSet()})"
    }

    override fun <R> mapNeighbors(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> =
        neighborsAsSequence().map { FieldEntry(it.id, transform(it)) }

    private fun neighborsAsSequence(): Sequence<FieldEntry<ID, T>> =
        neighborsIDs.map { FieldEntry(checkNotLocal(it), local.value) }
}

internal class PointwiseField<ID : Any, T>(context: Aggregate<ID>, localId: ID, default: T, localValue: T = default) :
    AbstractField<ID, T>(context, default, localId, localValue) {

    override val all: CollapseAll<FieldEntry<ID, T>>
        get() = object : CollapseAll<FieldEntry<ID, T>> {
            override val list get() = listOf(local)
            override val set get() = setOf(local)
            override val sequence get() = sequenceOf(local)
            override val size: Int get() = 1
        }

    override val asMap: Map<ID, T> get() = mapOf(local.pair)

    override val neighbors: CollapseNeighbors<FieldEntry<ID, T>>
        get() = object : CollapseNeighbors<FieldEntry<ID, T>> {
            override val list get() = emptyList<FieldEntry<ID, T>>()
            override val set get() = emptySet<FieldEntry<ID, T>>()
            override val sequence get() = emptySequence<FieldEntry<ID, T>>()
            override val size: Int get() = 0
        }

    override val stringRepresentation: String by lazy {
        "ϕ(default: $default, pointwise: $local)"
    }

    override fun <R> mapNeighbors(transform: (FieldEntry<ID, T>) -> R): Sequence<FieldEntry<ID, R>> = emptySequence()
}

private fun <ID : Any> Field<ID, *>.checkNotLocal(id: ID): ID {
    check(id != local.id) {
        "Local value $id is both local and neighboring. This is a bug in ${this::class.simpleName}"
    }
    return id
}
