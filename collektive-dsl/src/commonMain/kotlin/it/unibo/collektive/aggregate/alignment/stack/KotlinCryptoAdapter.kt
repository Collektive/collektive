/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment.stack

import it.unibo.collektive.aggregate.alignment.path.StringPath
import kotlin.collections.plus
import kotlin.uuid.Uuid
import org.kotlincrypto.core.digest.Digest

/**
 * Adapters plugging KotlinCrypto [Digest]s into Collektive alignment [it.unibo.collektive.aggregate.alignment.Stack]s.
 */
object KotlinCryptoAdapter {

    /**
     * Returns a [StackDeque.PathFactory] hashing the whole token list with a fresh digest from [digestFactory],
     * producing [StringPath]s.
     */
    fun pathFactoryOf(digestFactory: () -> Digest): StackDeque.PathFactory = { tokens ->
        StringPath(digestFactory().digestAny(tokens))
    }

    /**
     * Wraps [kotlinCryptodigest] into a [HashingStack.Digest].
     */
    fun digestOf(kotlinCryptodigest: Digest): HashingStack.Digest =
        object : HashingStack.ByteArrayDigest(kotlinCryptodigest.digestLength() * Byte.SIZE_BITS) {
            override fun reset() = kotlinCryptodigest.reset()

            override fun update(token: Any?) = token.hashable().forEach { kotlinCryptodigest.digestToken(it) }

            override fun update(value: Int) = kotlinCryptodigest.update(value.toUInt().convertToByteArray())

            override fun update(value: Long) = kotlinCryptodigest.update(value.toULong().convertToByteArray())

            override fun update(bytes: ByteArray) = kotlinCryptodigest.update(bytes)

            override fun bytes(): ByteArray = kotlinCryptodigest.digest()
        }

    /**
     * A [type] tag followed by its [value].
     * For containers, the [value] is the number of contained elements,
     * which are emitted as separate tokens right after the container.
     */
    private class HashableToken(val type: String, val value: ByteArray = byteArrayOf()) {
        operator fun plus(other: Iterable<HashableToken>): Iterable<HashableToken> = listOf(this) + other
    }

    /*
     * Containers are tagged by kind rather than by runtime class: collections that are equal must hash the same
     * regardless of their implementation (e.g., `listOf(1)` and `mutableListOf(1)`),
     * and the implementation classes may be anonymous on some platforms (e.g., Kotlin/Native and Kotlin/Wasm).
     */
    private fun Any?.hashable(): Iterable<HashableToken> = when (this) {
        null -> listOf(HashableToken(NULL_TAG))
        is Pair<*, *> -> HashableToken(PAIR_TAG) + first.hashable() + second.hashable()
        is Triple<*, *, *> -> HashableToken(TRIPLE_TAG) + first.hashable() + second.hashable() + third.hashable()
        is Iterable<*> -> toList().let { elements ->
            val tag = when (this) {
                is List<*> -> LIST_TAG
                is Set<*> -> SET_TAG
                else -> ITERABLE_TAG
            }
            HashableToken(tag, elements.size.toByteArray()) + elements.flatMap { it.hashable() }
        }
        is Map<*, *> -> HashableToken(MAP_TAG, size.toByteArray()) + flatMap { (k, v) -> (k to v).hashable() }
        else -> {
            val typeName = checkNotNull(this::class.simpleName) { "Hashing anonymous types is not supported" }
            listOf(HashableToken(typeName, toByteArray()))
        }
    }

    private const val NULL_TAG = "⭕"
    private const val PAIR_TAG = "Pair"
    private const val TRIPLE_TAG = "Triple"
    private const val LIST_TAG = "List"
    private const val SET_TAG = "Set"
    private const val ITERABLE_TAG = "Iterable"
    private const val MAP_TAG = "Map"

    private fun UInt.convertToByteArray(): ByteArray =
        ByteArray(UInt.SIZE_BYTES) { i -> (this shr (i * Byte.SIZE_BITS)).toByte() }
    private fun ULong.convertToByteArray(): ByteArray =
        ByteArray(ULong.SIZE_BYTES) { i -> (this shr (i * Byte.SIZE_BITS)).toByte() }
    private fun Any.toByteArray(): ByteArray = when (this) {
        // Types directly mapped to a byte array
        is Boolean -> if (this) byteArrayOf(1) else byteArrayOf(0)
        is Byte -> byteArrayOf(this)
        is UInt -> this.convertToByteArray()
        is ULong -> this.convertToByteArray()
        is String -> this.encodeToByteArray()
        else -> toConvertedByteArray()
    }
    private fun Any.toConvertedByteArray(): ByteArray = when (this) {
        is UByte -> this.toByte().toByteArray()
        is Short -> this.toInt().toByteArray()
        is UShort -> this.toInt().toByteArray()
        is Int -> this.toUInt().toByteArray()
        is Long -> this.toULong().toByteArray()
        is Float -> this.toRawBits().toByteArray()
        is Double -> this.toRawBits().toByteArray()
        is Char -> this.toString().toByteArray()
        is Uuid -> this.toString().encodeToByteArray()
        else -> error("Alignment on elements of type ${this::class.simpleName} is not supported")
    }

    /*
     * Both the type and the value are length-prefixed, so that the boundaries between tokens are unambiguous
     * (e.g., the strings "x" and "y" must not hash as the single string "xStringy").
     */
    private fun Digest.digestToken(token: HashableToken) {
        updateWithLength(token.type.encodeToByteArray())
        updateWithLength(token.value)
    }

    private fun Digest.updateWithLength(bytes: ByteArray) {
        update(bytes.size.toUInt().convertToByteArray())
        update(bytes)
    }
    private fun Digest.digestAny(any: Any?): ByteArray = apply { any.hashable().forEach { digestToken(it) } }.digest()
}
