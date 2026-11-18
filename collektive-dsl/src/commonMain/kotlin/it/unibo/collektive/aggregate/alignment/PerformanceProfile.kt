/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate.alignment

import it.unibo.collektive.aggregate.alignment.path.FullPath
import it.unibo.collektive.aggregate.alignment.path.IntPath
import it.unibo.collektive.aggregate.alignment.stack.HashingStack
import it.unibo.collektive.aggregate.alignment.stack.KotlinCryptoAdapter
import it.unibo.collektive.aggregate.alignment.stack.NonCryptoHashAdapter
import it.unibo.collektive.aggregate.alignment.stack.StackDeque
import org.danilopianini.noncryptohash.Digest as NonCryptoDigest
import org.danilopianini.noncryptohash.murmur3.MurmurHash128Digest
import org.danilopianini.noncryptohash.xxhash.XxHash32Digest
import org.kotlincrypto.core.digest.Digest as KotlinCryptoDigest
import org.kotlincrypto.hash.sha3.Keccak512

/**
 * Strategy selecting how alignment [Stack]s are built, trading off memory footprint, speed,
 * collision resistance, and serializability of the resulting [Path]s.
 * Every invocation must return a fresh [Stack], as a new one is created for each round.
 */
fun interface PerformanceProfile {

    /**
     * Creates a new, empty [Stack].
     */
    operator fun invoke(): Stack

    /**
     * Keeps the full list of alignment tokens in every path.
     * Fastest and collision-free, but paths are not serializable:
     * suitable for in-memory simulations only.
     */
    object ForSimulation : PerformanceProfile by PerformanceProfile({ StackDeque { FullPath(it) } })

    /**
     * Profiles producing serializable paths, suitable for devices exchanging messages over a real network.
     */
    object ForDeployment {

        /**
         * Keeps the full token stack, and hashes it from scratch with a cryptographic digest
         * (created by [digestFactory], [Keccak512] by default) every time a path is requested.
         * Produces serializable paths with negligible collision probability, at the cost of performance.
         */
        class WithCryptographicPaths(digestFactory: () -> KotlinCryptoDigest = { Keccak512() }) :
            PerformanceProfile by PerformanceProfile({ StackDeque(KotlinCryptoAdapter.pathFactoryOf(digestFactory)) })

        /**
         * Keeps the full token stack, and hashes it from scratch with a non-cryptographic digest
         * (created by [digestFactory], 32-bit XXHash by default) every time a path is requested.
         * Faster than [WithCryptographicPaths], but with no protection against crafted collisions.
         */
        class Fast(digestFactory: () -> NonCryptoDigest = { XxHash32Digest() }) :
            PerformanceProfile by PerformanceProfile({ StackDeque(NonCryptoHashAdapter.pathFactoryOf(digestFactory)) })

        /**
         * Profiles that do not keep the aligned tokens in memory, but only one hash per nesting level,
         * each computed from its parent hash and the newly aligned token
         * (see [HashingStack]).
         */
        object LowMemory {
            /**
             * Low-memory profile backed by a cryptographic digest created by [digestFactory].
             * The digest must produce more than 64 bits.
             */
            class WithCryptoHash(digestFactory: () -> KotlinCryptoDigest) :
                PerformanceProfile by PerformanceProfile({
                    HashingStack(KotlinCryptoAdapter.digestOf(digestFactory()))
                })

            /**
             * Low-memory profile backed by a non-cryptographic digest created by [digestFactory].
             * Faster than [WithCryptoHash], but with no protection against crafted collisions.
             */
            class WithNonCryptoHash(digestFactory: () -> NonCryptoDigest) :
                PerformanceProfile by PerformanceProfile({
                    HashingStack(NonCryptoHashAdapter.digestOf(digestFactory()))
                })

            /**
             * Low-memory profile based on 32-bit XXHash, which avoids 64-bit arithmetic
             * (slow on Kotlin/JS); paths are [IntPath]s.
             */
            object JsFriendly : PerformanceProfile by WithNonCryptoHash({ XxHash32Digest() })

            /**
             * Low-memory profile based on 128-bit MurmurHash3.
             */
            object Fast : PerformanceProfile by WithNonCryptoHash({ MurmurHash128Digest() })
        }
    }
}
