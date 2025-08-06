/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

/**
 * A collapsing view over a collection of [size] elements that can be accessed as a [list], [set], or [sequence].
 *
 * This abstraction is used to represent either the neighborhood of a field (peers), or the neighborhood
 * plus the local element (with self), with different subtypes conveying inclusion semantics.
 *
 * @param E the element type contained in the collapse.
 */
sealed interface Collapse<out E> {
    val list: List<E>
    val set: Set<E>
    val sequence: Sequence<E>
    val size: Int
}

/**
 * A [Collapse] that represents only the peers (excluding the local element).
 *
 * @param E the element type contained in the collapse.
 */
interface CollapsePeers<out E> : Collapse<E>

/**
 * A [Collapse] that include the local element in addition to peers.
 *
 * @param E the element type contained in the collapse.
 */
interface CollapseWithSelf<out E> : Collapse<E>

private interface AnyCollapse<out E> :
    CollapsePeers<E>,
    CollapseWithSelf<E>

/**
 * Checks if the given [element] is present in this collapse.
 */
operator fun <T> Collapse<T>.contains(element: T): Boolean = set.contains(element)

/**
 * Converts this collapse of field entries into a map from entry IDs to their values.
 *
 * Each entry in the collapse is turned into an entry in the resulting map using its `id`
 * as the key and its `value` as the value.
 *
 * @param ID the type used to identify entries.
 * @param T the type of value carried by each entry.
 * @return a map associating each entry ID with its corresponding value.
 */
fun <ID : Any, T> Collapse<FieldEntry<ID, T>>.toMap(): Map<ID, T> = sequence.associate { it.pair }

/**
 * Returns a collapse containing only the IDs from this [CollapseWithSelf].
 *
 * @return a collapse over the IDs present in the original [CollapseWithSelf].
 */
val <ID : Any> CollapseWithSelf<FieldEntry<ID, *>>.ids: CollapseWithSelf<ID> get() = sequence.ids()

/**
 * Returns a collapse containing only the IDs from this [CollapsePeers].
 *
 * @return a collapse over the IDs present in the original [CollapsePeers].
 */
val <ID : Any> CollapsePeers<FieldEntry<ID, *>>.ids: CollapsePeers<ID> get() = sequence.ids()

/**
 * Returns a collapse containing only the values from this [CollapseWithSelf].
 *
 * @return a collapse over the values present in the original [CollapseWithSelf].
 */
val <T> CollapseWithSelf<FieldEntry<*, T>>.values: CollapseWithSelf<T> get() = sequence.values()

/**
 * Returns a collapse containing only the values from this [CollapsePeers].
 *
 * @return a collapse over the values present in the original [CollapsePeers].
 */
val <T> CollapsePeers<FieldEntry<*, T>>.values: CollapsePeers<T> get() = sequence.values()

internal open class ListBackedCollapse<out T>(override val list: List<T>) : AnyCollapse<T> {
    override val set by lazy { list.toSet() }
    override val sequence get() = list.asSequence()
    override val size: Int get() = list.size
}

internal open class SequenceBackedCollapse<out T>(val origin: Sequence<T>) : AnyCollapse<T> {
    private var consumed = false
    override val list: List<T> by lazy {
        when {
            consumed -> set.toList()
            else -> {
                consumed = true
                origin.toList()
            }
        }
    }
    override val set: Set<T> by lazy {
        when {
            consumed -> list.toSet()
            else -> {
                consumed = true
                origin.toSet()
            }
        }
    }
    override val sequence: Sequence<T> get() = when {
        consumed -> list.asSequence()
        else -> origin
    }
    override val size: Int get() = list.size
}

private fun <ID : Any> Sequence<FieldEntry<ID, *>>.ids() = SequenceBackedCollapse(map { it.id })
private fun <T> Sequence<FieldEntry<*, T>>.values() = SequenceBackedCollapse(map { it.value })
