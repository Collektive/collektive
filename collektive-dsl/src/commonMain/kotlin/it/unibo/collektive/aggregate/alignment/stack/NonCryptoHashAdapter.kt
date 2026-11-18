/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment.stack

import it.unibo.collektive.aggregate.alignment.path.StringPath
import kotlin.io.encoding.Base64
import org.danilopianini.noncryptohash.Digest
import org.danilopianini.noncryptohash.FunnelRegistry
import org.danilopianini.noncryptohash.common.updateValue

/**
 * Adapters plugging noncrypto-hash [Digest]s into Collektive alignment
 * [Stacks][it.unibo.collektive.aggregate.alignment.Stack].
 */
object NonCryptoHashAdapter {

    /**
     * Returns a [StackDeque.PathFactory] hashing the whole token list with a fresh digest from [digestFactory],
     * producing [StringPath]s.
     */
    fun pathFactoryOf(digestFactory: () -> Digest): StackDeque.PathFactory = { tokens ->
        StringPath(
            Base64.encode(
                digestFactory().apply { updateValue(tokens, FunnelRegistry.EMPTY) }.digest(),
            ),
        )
    }

    /**
     * Wraps [nonCryptoDigest] into a [HashingStack.Digest].
     * Tokens are fed through [updateValue] with an empty [FunnelRegistry].
     */
    fun digestOf(nonCryptoDigest: Digest): HashingStack.Digest = object : HashingStack.Digest {
        override val bits: Int = nonCryptoDigest.bits

        override fun reset() {
            nonCryptoDigest.reset()
        }

        override fun update(token: Any?) {
            context(FunnelRegistry.EMPTY) {
                nonCryptoDigest.updateValue(token)
            }
        }

        override fun update(value: Int) {
            nonCryptoDigest.update(value)
        }

        override fun update(value: Long) {
            nonCryptoDigest.update(value)
        }

        override fun update(bytes: ByteArray) {
            nonCryptoDigest.update(bytes)
        }

        override fun int(): Int = nonCryptoDigest.digestInt()

        override fun long(): Long = nonCryptoDigest.digestLong()

        override fun bytes(): ByteArray = nonCryptoDigest.digest()
    }
}
