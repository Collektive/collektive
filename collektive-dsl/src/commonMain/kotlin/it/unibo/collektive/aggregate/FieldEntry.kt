/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import kotlin.jvm.JvmInline

/**
 * The entry of a [Field].
 *
 * @param ID device identifier type
 * @param T the type of the value
 * @property pair a pair version of this entry
 */
@JvmInline
value class FieldEntry<ID : Any, out T>(val pair: Pair<ID, T>) {

    /**
     * Creates a new [FieldEntry] with the given [id] and [value].
     */
    constructor(id: ID, value: T) : this(id to value)

    /**
     * Creates a new [FieldEntry] with the given [entry].
     */
    constructor(entry: Map.Entry<ID, T>) : this(entry.toPair())

    /**
     * The device ID of this entry.
     */
    val id: ID get() = pair.first

    /**
     * The value of this entry.
     */
    val value: T get() = pair.second

    /**
     * The [id].
     */
    operator fun component1(): ID = id

    /**
     * The [value].
     */
    operator fun component2(): T = value

    /**
     * Returns a [FieldEntry] with the same [id] and a transformed [value].
     */
    fun <R> map(transform: (FieldEntry<ID, T>) -> R): FieldEntry<ID, R> = FieldEntry(id, transform(this))

    /**
     * Returns a [FieldEntry] with the same [id] and a transformed [value].
     */
    fun <R> mapValue(transform: (T) -> R): FieldEntry<ID, R> = FieldEntry(id, transform(value))
}

/**
 * Converts a [Map.Entry] to a [FieldEntry].
 */
fun <ID : Any, T> Map.Entry<ID, T>.toFieldEntry(): FieldEntry<ID, T> = FieldEntry(this.key, this.value)

/**
 * Converts a [Pair] to a [FieldEntry].
 */
fun <ID : Any, T> Pair<ID, T>.toFieldEntry(): FieldEntry<ID, T> = FieldEntry(this)

/**
 * Converts a Sequence of [FieldEntry] to a Map<ID, T>.
 */
fun <ID : Any, T> Sequence<FieldEntry<ID, T>>.toMap(): Map<ID, T> = associate { it.id to it.value }
