/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmInline

/**
 * A path represents a specific point in the AST of an aggregate program.
 * The point in the AS is identified as a sequence of tokens.
 */
@Serializable(with = PathSerializer::class)
sealed interface Path {

    /**
     * Returns a multiline string representation of the path.
     */
    fun toMultilineString(separator: String = "\n"): String = toString()
}

/**
 * Non-serializable, list-based, high performance path intended for use in simulated environments.
 * Unsuitable for practical applications.
 */
data class FullPath(private val path: List<Any?>) : Path {
    private val hash = path.hashCode()

    override fun hashCode(): Int = hash

    override fun equals(other: Any?): Boolean = when {
        this === other -> true
        other !is FullPath -> false
        else -> hash == other.hashCode() && path == other.path
    }

    override fun toMultilineString(separator: String): String = path
        .mapIndexed { i, path ->
            when {
                i == 0 -> path
                else -> "${generateSequence("") { "  " }.take(i).joinToString("")}↸ $path"
            }
        }
        .joinToString(separator = separator)

    /**
     * Utility companion object.
     */
    companion object {
        private val cache = mutableMapOf<List<Any?>, Path>()
        private const val MAX_CACHE_SIZE = 10_000
        private const val CACHE_CLEANUP_SIZE = 1000

        /**
         * Creates a new [Path] from the given [path].
         */
        fun of(path: List<Any?>): Path = cache.getOrPut(path) { FullPath(path) }.also {
            if (cache.size >= MAX_CACHE_SIZE) {
                val iterator = cache.iterator()
                repeat(CACHE_CLEANUP_SIZE) {
                    iterator.next()
                    iterator.remove()
                }
            }
        }
    }
}

/**
 * Serializable [Path] implementation that wraps a [backend] object.
 */
@JvmInline
@Serializable
value class SerializablePath(val backend: String) : Path

private object PathSerializer : KSerializer<Path> {
    override val descriptor: SerialDescriptor
        get() = SerializablePath.serializer().descriptor

    override fun serialize(encoder: Encoder, value: Path) {
        when (value) {
            is SerializablePath -> SerializablePath.serializer().serialize(encoder, value)
            else -> throw IllegalArgumentException("Cannot serialize $value")
        }
    }

    override fun deserialize(decoder: Decoder): Path = SerializablePath.serializer().deserialize(decoder)
}
