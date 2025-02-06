/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import it.unibo.collektive.path.impl.SerializablePath
import org.kotlincrypto.hash.sha3.Keccak512
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.jvm.JvmInline

/**
 * A factory that creates [Path] instances by hashing their content.
 */
interface Digest {
    /**
     * Updates the digest with the given [data].
     */
    fun update(data: ByteArray)

    /**
     * Returns the digest of the data passed to [update].
     */
    fun digest(): ByteArray

    /**
     * Returns the digest as a string.
     */
    fun digestToString(): String
}

/**
 * A factory that creates [Path] instances by hashing their content with a specific [backend].
 */
@JvmInline
value class KotlinCryptoDigest(
    val backend: org.kotlincrypto.core.digest.Digest,
) : Digest {
    override fun update(data: ByteArray) {
        backend.update(data)
    }

    override fun digest(): ByteArray = backend.digest()

    override fun digestToString(): String {
        @OptIn(ExperimentalEncodingApi::class)
        return Base64.encode(digest())
    }
}

/**
 * A factory that creates [Path] instances by hashing their content with a specific [digest].
 */
@JvmInline
value class DigestHashingFactory(
    val digest: Digest = KotlinCryptoDigest(Keccak512()),
) : PathFactory {
    override fun invoke(tokens: List<Any?>): Path {
        val hashedTokens = digest.apply { tokens.hashable().forEach { digest(it) } }.digest()

        @OptIn(ExperimentalEncodingApi::class)
        val base64encoding = Base64.encode(hashedTokens)
        return SerializablePath(base64encoding)
    }

    private fun Digest.digest(token: HashableToken) {
        update(token.type.encodeToByteArray())
        update(token.value)
    }

    private class HashableToken(
        val type: String,
        val value: ByteArray = byteArrayOf(),
    ) {
        operator fun plus(other: Iterable<HashableToken>): Iterable<HashableToken> = listOf(this) + other
    }

    private fun Any?.hashable(): Iterable<HashableToken> {
        val typeName = if (this == null) "⭕" else this::class.simpleName
        checkNotNull(typeName) {
            "Hashing anonymous types is not supported"
        }
        return when (this) {
            null -> listOf(HashableToken(typeName))
            is Pair<*, *> -> HashableToken(typeName) + first.hashable() + second.hashable()
            is Triple<*, *, *> -> HashableToken(typeName) + first.hashable() + second.hashable() + third.hashable()
            is Iterable<*> -> HashableToken(typeName) + this.flatMap { it.hashable() }
            is Map<*, *> -> HashableToken(typeName) + flatMap { (k, v) -> (k to v).hashable() }
            else -> listOf(HashableToken(typeName, this.toByteArray()))
        }
    }

    private fun Any.toConvertedByteArray(): ByteArray =
        when (this) {
            is UByte -> this.toByte().toByteArray()
            is Short -> this.toInt().toByteArray()
            is UShort -> this.toInt().toByteArray()
            is Int -> this.toUInt().toByteArray()
            is Long -> this.toULong().toByteArray()
            is Float -> this.toRawBits().toByteArray()
            is Double -> this.toRawBits().toByteArray()
            is Char -> this.toString().toByteArray()
            else -> error("Alignment on elements of type ${this::class.simpleName} is not supported")
        }

    private fun Any.toByteArray(): ByteArray =
        when (this) {
            // Types directly mapped to a byte array
            is Boolean -> if (this) byteArrayOf(1) else byteArrayOf(0)
            is Byte -> byteArrayOf(this)
            is UInt -> this.convertToByteArray()
            is ULong -> this.convertToByteArray()
            is String -> this.encodeToByteArray()
            else -> toConvertedByteArray()
        }

    private fun UInt.convertToByteArray(): ByteArray =
        ByteArray(UInt.SIZE_BYTES) { i -> (this shr (i * Byte.SIZE_BITS)).toByte() }

    private fun ULong.convertToByteArray(): ByteArray =
        ByteArray(ULong.SIZE_BYTES) { i -> (this shr (i * Byte.SIZE_BITS)).toByte() }
}
