/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment.stack

import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.Stack
import it.unibo.collektive.aggregate.alignment.path.IntPath
import it.unibo.collektive.aggregate.alignment.path.LongPath
import it.unibo.collektive.aggregate.alignment.path.StringPath

/**
 * [Stack] that does not retain aligned tokens, but only a chain of hashes: the path after aligning a token is
 * the hash of the parent path concatenated with the token, and dealigning restores the parent path.
 * Memory usage is thus proportional to the nesting depth times the [digest] size, regardless of token size.
 * Paths are [IntPath]s, [LongPath]s, or [StringPath]s depending on the size of the digest.
 */
class HashingStack(private val digest: Digest) : Stack {
    private val ancestors = ArrayDeque<Frame>()
    private var current: Frame = digest.apply { reset() }.toFrame()

    override fun currentPath(): Path = current.path

    override fun alignRaw(token: Any?) {
        digest.reset()
        current.feedTo(digest)
        digest.update(token)
        ancestors.addLast(current)
        current = digest.toFrame()
    }

    override fun dealign() {
        current = ancestors.removeLast()
    }

    override fun toString(): String = "${this::class.simpleName}(depth=${ancestors.size}, path=${current.path})"

    /**
     * A node of the hash chain: its [path], and the raw [bytes] of the hash when it does not fit a [Long].
     */
    private class Frame(val path: Path, val bytes: ByteArray?) {
        fun feedTo(digest: Digest) {
            when (path) {
                is IntPath -> digest.update(path.backend)
                is LongPath -> digest.update(path.backend)
                else -> digest.update(checkNotNull(bytes))
            }
        }
    }

    private fun Digest.toFrame(): Frame = when (bits) {
        in 1..Int.SIZE_BITS -> Frame(IntPath(int()), null)
        in Int.SIZE_BITS + 1..Long.SIZE_BITS -> Frame(LongPath(long()), null)
        else -> bytes().let { Frame(StringPath(it), it) }
    }

    /**
     * Minimal incremental hash function abstraction used by [HashingStack].
     * Reading the hash (via [int], [long], or [bytes]) may reset the digest:
     * [HashingStack] always calls [reset] before feeding new data.
     */
    interface Digest {
        /**
         * The size of the produced hash, in bits.
         */
        val bits: Int

        /**
         * Resets the digest to its initial state.
         */
        fun reset()

        /**
         * Feeds an arbitrary [token] to the digest.
         */
        fun update(token: Any?)

        /**
         * Feeds an [Int] [value] to the digest.
         */
        fun update(value: Int)

        /**
         * Feeds a [Long] [value] to the digest.
         */
        fun update(value: Long)

        /**
         * Feeds the given [bytes] to the digest.
         */
        fun update(bytes: ByteArray)

        /**
         * Returns the current hash, truncated to an [Int] if larger.
         */
        fun int(): Int

        /**
         * Returns the current hash, truncated to a [Long] if larger.
         */
        fun long(): Long

        /**
         * Returns the current hash as a byte array of [bits] / 8 elements.
         */
        fun bytes(): ByteArray
    }

    /**
     * Base [Digest] for hashes of at most 64 [bits], which derives [bytes] from [long] (little-endian).
     */
    abstract class PrimitiveDigest(final override val bits: Int) : Digest {

        init {
            require(bits in 1..Long.SIZE_BITS) { "Bits must be in 1..${Long.SIZE_BITS}, was $bits" }
            require(bits % Byte.SIZE_BITS == 0) { "The hash size must be a multiple of ${Byte.SIZE_BITS} bits" }
        }

        final override fun bytes() = long().let { data ->
            ByteArray(bits / Byte.SIZE_BITS) { (data shr Byte.SIZE_BITS * it).toByte() }
        }
    }

    /**
     * [PrimitiveDigest] producing 32-bit hashes; implementors only need to provide [int].
     */
    abstract class Digest32 : PrimitiveDigest(Int.SIZE_BITS) {
        final override fun long() = int().toLong()
    }

    /**
     * [PrimitiveDigest] producing 64-bit hashes; implementors only need to provide [long].
     */
    abstract class Digest64 : PrimitiveDigest(Long.SIZE_BITS) {
        final override fun int() = long().toInt()
    }

    /**
     * Base [Digest] for hashes larger than 64 [bits], which derives [int] and [long]
     * from the leading bytes of [bytes] (big-endian).
     */
    abstract class ByteArrayDigest(final override val bits: Int) : Digest {

        init {
            require(bits > Long.SIZE_BITS) { "Bit size must be greater than ${Long.SIZE_BITS}, was: $bits" }
        }

        final override fun int() = bytes().bigEndian(Int.SIZE_BYTES).toInt()

        final override fun long() = bytes().bigEndian(Long.SIZE_BYTES)

        private fun ByteArray.bigEndian(count: Int): Long {
            require(size >= count) { "At least $count bytes are required, but the digest produced $size" }
            var result = 0L
            for (index in 0 until count) {
                result = (result shl Byte.SIZE_BITS) or (this[index].toLong() and BYTE_MASK)
            }
            return result
        }
    }

    private companion object {
        private const val BYTE_MASK = 0xFFL
    }
}
