/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.benchmarks

import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.PerformanceProfile
import it.unibo.collektive.aggregate.alignment.Stack
import it.unibo.collektive.aggregate.alignment.path.FullPath
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import org.danilopianini.noncryptohash.fnv.Fnv1a32Digest
import org.kotlincrypto.hash.sha3.Keccak512

/**
 * All the profiles under comparison, by name.
 * Besides the ones shipped with Collektive, it includes candidate alternatives to
 * [PerformanceProfile.ForSimulation] that avoid recomputing the hash of the whole token list on every path request.
 */
val profiles: Map<String, PerformanceProfile> = mapOf(
    "ForSimulation" to PerformanceProfile.ForSimulation,
    "ForSimulationPersistentList" to PerformanceProfile { PersistentListStack() },
    "ForSimulationConsList" to PerformanceProfile { ConsStack(Polynomial31) },
    "ForSimulationConsListFnv1a32" to PerformanceProfile { ConsStack(Fnv1a32Combiner()) },
    "ForSimulationArrayList" to PerformanceProfile { ArrayListStack(Polynomial31) },
    "ForSimulationArrayListFnv1a32" to PerformanceProfile { ArrayListStack(Fnv1a32Combiner()) },
    "SafeDeployment" to PerformanceProfile.ForDeployment.WithCryptographicPaths(),
    "ForDeploymentFast" to PerformanceProfile.ForDeployment.Fast(),
    "LowMemoryFast" to PerformanceProfile.ForDeployment.LowMemory.Fast,
    "LowMemoryJsFriendly" to PerformanceProfile.ForDeployment.LowMemory.JsFriendly,
    "LowMemoryKeccak512" to PerformanceProfile.ForDeployment.LowMemory.WithCryptoHash { Keccak512() },
)

/**
 * Strategy computing the hash of a path from the hash of its parent and the newly aligned token.
 * Implementations may be stateful, and must hence not be shared among stacks.
 */
fun interface HashCombiner {
    /**
     * The hash of the path obtained aligning [token] on a path whose hash is [parent].
     */
    fun combine(parent: Int, token: Any?): Int

    /**
     * Shared constants.
     */
    companion object {
        /**
         * The hash of the empty path.
         */
        const val ROOT_HASH: Int = 1
    }
}

/**
 * The polynomial combination used by [List.hashCode]: `31 * parent + token.hashCode()`.
 */
object Polynomial31 : HashCombiner {
    private const val MULTIPLIER = 31

    override fun combine(parent: Int, token: Any?): Int = MULTIPLIER * parent + token.hashCode()
}

/**
 * FNV-1a (32 bit) over the eight bytes of the parent hash and of the token hash.
 */
class Fnv1a32Combiner : HashCombiner {
    private val digest = Fnv1a32Digest()

    override fun combine(parent: Int, token: Any?): Int =
        digest.reset().update(parent).update(token.hashCode()).digestInt()
}

/**
 * Candidate full-path stack backed by a [PersistentList]: snapshots are free,
 * but [FullPath] still computes the hash over the whole list.
 */
class PersistentListStack : Stack {
    private var tokens: PersistentList<Any?> = persistentListOf()

    override fun currentPath(): Path = FullPath(tokens)

    override fun alignRaw(token: Any?) {
        tokens = tokens.adding(token)
    }

    override fun dealign() {
        tokens = tokens.removingAt(tokens.lastIndex)
    }
}

/**
 * Candidate full-path stack backed by an immutable linked list with an incrementally computed hash
 * (according to the [combiner]): aligning, dealigning, and requesting the path are all O(1);
 * equality is O(depth) only on hash match.
 */
class ConsStack(private val combiner: HashCombiner) : Stack {
    private var current: ConsPath = ConsPath.ROOT

    override fun currentPath(): Path = current

    override fun alignRaw(token: Any?) {
        current = ConsPath(current, token, combiner.combine(current.hashCode(), token))
    }

    override fun dealign() {
        current = checkNotNull(current.parent) { "Cannot dealign the root path" }
    }
}

/**
 * A [Path] node pointing to its [parent], holding the last aligned [token].
 *
 * @property parent the path this one extends, `null` only for the root.
 * @property token the last aligned token.
 * @property depth the number of tokens in the path.
 */
class ConsPath private constructor(val parent: ConsPath?, val token: Any?, val depth: Int, private val hash: Int) :
    Path {
    /**
     * Extends [parent] with [token], with the precomputed [hash].
     */
    constructor(parent: ConsPath, token: Any?, hash: Int) : this(parent, token, parent.depth + 1, hash)

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean {
        if (other !is ConsPath || other.hash != hash || other.depth != depth) return false
        var mine: ConsPath = this
        var theirs: ConsPath = other
        while (mine !== theirs) {
            if (mine.token != theirs.token) return false
            mine = mine.parent ?: return theirs.parent == null
            theirs = theirs.parent ?: return false
        }
        return true
    }

    override fun toString(): String = generateSequence(this) { it.parent }
        .filter { it.parent != null }
        .map { it.token }
        .toList()
        .asReversed()
        .toString()

    internal companion object {
        /**
         * The empty path.
         */
        val ROOT = ConsPath(null, null, 0, HashCombiner.ROOT_HASH)
    }
}

/**
 * Candidate full-path stack backed by an [ArrayList] of (token, hash of the path up to the token) pairs,
 * used as a deque: aligning and dealigning are amortized O(1),
 * requesting the path costs a copy of the list (an array copy), but no hashing.
 */
class ArrayListStack(private val combiner: HashCombiner) : Stack {
    private val entries = ArrayList<Pair<Any?, Int>>()

    private val currentHash: Int get() = entries.lastOrNull()?.second ?: HashCombiner.ROOT_HASH

    override fun currentPath(): Path = ListPath(entries.toList(), currentHash)

    override fun alignRaw(token: Any?) {
        entries.add(token to combiner.combine(currentHash, token))
    }

    override fun dealign() {
        entries.removeAt(entries.lastIndex)
    }
}

/**
 * A [Path] snapshot of the [entries] of an [ArrayListStack], whose hash is precomputed.
 */
class ListPath(private val entries: List<Pair<Any?, Int>>, private val hash: Int) : Path {
    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean =
        this === other || other is ListPath && other.hash == hash && other.entries == entries

    override fun toString(): String = entries.map { it.first }.toString()
}
