package it.unibo.collektive.path

import it.unibo.collektive.path.impl.FullPath
import it.unibo.collektive.path.impl.StringPath
import org.kotlincrypto.core.Updatable
import org.kotlincrypto.hash.sha3.Keccak512
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * A path represents a specific point in the AST of an aggregate program.
 * The point in the AS is identified as a sequence of tokens.
 */
interface PathFactory {
    /**
     * Creates a new [Path] from the given [tokens].
     */
    operator fun invoke(vararg tokens: Any?): Path = invoke(tokens.toList())

    /**
     * Creates a new [Path] from the given [tokens].
     */
    operator fun invoke(tokens: List<Any?>): Path

    /**
     * Factory of [Path]s using an explicit representation for the path tokens.
     */
    object FullPathFactory : PathFactory {
        override fun invoke(tokens: List<Any?>): Path = FullPath(tokens)
    }

    /**
     * Factory of [Path]s using a non-cryptographic hashing algorithm to generate the path.
     */
    object NonCryptographicHashingFactory : PathFactory {
        override fun invoke(tokens: List<Any?>): Path =
            object : Path {
                init {
                    TODO()
                }
            }
    }

    /**
     * Factory of [Path]s using a cryptographic hashing algorithm to generate the path.
     */
    object CryptographicHashingFactory : PathFactory {
        @OptIn(ExperimentalEncodingApi::class)
        override fun invoke(tokens: List<Any?>): Path =
            StringPath(
                Base64.Default.encode(
                    Keccak512()
                        .apply {
                            tokens.hashable().forEach { digest(it) }
                        }.digest(),
                ),
            )
    }

    private companion object {
        fun Updatable.digest(token: HashableToken) {
            update(token.type.encodeToByteArray())
            update(token.value)
        }

        class HashableToken(
            val type: String,
            val value: ByteArray = byteArrayOf(),
        ) {
            operator fun plus(other: Iterable<HashableToken>): Iterable<HashableToken> = listOf(this) + other
        }

        fun Any?.hashable(): Iterable<HashableToken> {
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

        fun Any.toConvertedByteArray(): ByteArray =
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

        fun Any.toByteArray(): ByteArray =
            when (this) {
                // Types directly mapped to a byte array
                is Boolean -> if (this) byteArrayOf(1) else byteArrayOf(0)
                is Byte -> byteArrayOf(this)
                is UInt -> this.convertToByteArray()
                is ULong -> this.convertToByteArray()
                is String -> this.encodeToByteArray()
                else -> toConvertedByteArray()
            }

        fun UInt.convertToByteArray(): ByteArray =
            ByteArray(UInt.SIZE_BYTES) { i -> (this shr (i * Byte.SIZE_BITS)).toByte() }

        fun ULong.convertToByteArray(): ByteArray =
            ByteArray(ULong.SIZE_BYTES) { i -> (this shr (i * Byte.SIZE_BITS)).toByte() }
    }
}
