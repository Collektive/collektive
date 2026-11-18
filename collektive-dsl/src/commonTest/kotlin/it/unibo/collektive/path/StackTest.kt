/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import it.unibo.collektive.aggregate.alignment.Path
import it.unibo.collektive.aggregate.alignment.PerformanceProfile
import it.unibo.collektive.aggregate.alignment.stack.KotlinCryptoAdapter
import it.unibo.collektive.testing.forEachPerformanceProfile
import it.unibo.collektive.testing.testedPerformanceProfiles
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import org.kotlincrypto.hash.sha3.Keccak512

class StackTest {

    private val allProfiles: List<PerformanceProfile> = testedPerformanceProfiles + listOf(
        PerformanceProfile.ForDeployment.LowMemory.JsFriendly,
        PerformanceProfile.ForDeployment.LowMemory.WithCryptoHash { Keccak512() },
    )

    private fun PerformanceProfile.pathOf(vararg tokens: Any?): Path = invoke().apply {
        tokens.forEach { alignRaw(it) }
    }.currentPath()

    @Test
    fun `dealigning restores the previous path`() {
        forEachPerformanceProfile(allProfiles) { profile ->
            val stack = profile()
            val root = stack.currentPath()
            stack.alignRaw("a")
            val a = stack.currentPath()
            stack.alignRaw(1)
            stack.dealign()
            assertEquals(a, stack.currentPath())
            stack.dealign()
            assertEquals(root, stack.currentPath())
        }
    }

    @Test
    fun `paths do not depend on previously closed alignments`() {
        forEachPerformanceProfile(allProfiles) { profile ->
            val stack = profile()
            stack.alignRaw("branch")
            stack.alignRaw(true)
            stack.dealign()
            stack.dealign()
            stack.alignRaw("next")
            assertEquals(profile.pathOf("next"), stack.currentPath())
        }
    }

    @Test
    fun `sibling and nested alignments produce different paths`() {
        forEachPerformanceProfile(allProfiles) { profile ->
            val paths = listOf(
                profile.pathOf(),
                profile.pathOf("a"),
                profile.pathOf("b"),
                profile.pathOf("a", "b"),
                profile.pathOf("b", "a"),
                profile.pathOf("a", "a"),
            )
            assertEquals(paths.size, paths.toSet().size, "Collision among $paths")
        }
    }

    @Test
    fun `cryptographic hashing does not confuse token boundaries`() {
        val factory = KotlinCryptoAdapter.pathFactoryOf { Keccak512() }
        assertNotEquals(factory(listOf("x", "y")), factory(listOf("xStringy")))
        assertNotEquals(factory(listOf(listOf(1), 2)), factory(listOf(listOf(1, 2))))
        val digest = KotlinCryptoAdapter.digestOf(Keccak512())
        fun hashOf(token: Any?) = digest.run {
            reset()
            update(token)
            bytes().toList()
        }
        assertNotEquals(hashOf(listOf(listOf(1), 2)), hashOf(listOf(listOf(1, 2))))
    }

    @Test
    fun `cryptographic hashing depends on the content of collections and not on their implementation`() {
        val factory = KotlinCryptoAdapter.pathFactoryOf { Keccak512() }
        assertEquals(factory(listOf(listOf(1))), factory(listOf(mutableListOf(1))))
        assertEquals(factory(listOf(listOf(1, 2))), factory(listOf(ArrayList(listOf(1, 2)))))
        assertEquals(factory(listOf(mapOf(1 to 2))), factory(listOf(hashMapOf(1 to 2))))
        assertNotEquals(factory(listOf(listOf(1))), factory(listOf(setOf(1))))
    }
}
