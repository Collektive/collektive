/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.branch

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.alignment.PerformanceProfile
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.testing.forEachPerformanceProfile
import kotlin.test.Test
import kotlin.test.assertEquals

class WhenTest {
    private fun programUnderTest(input: Any, profile: PerformanceProfile) = aggregate(0, performanceProfile = profile) {
        val stringVal = "string"
        val testVal = "test"
        when (input) {
            is String -> neighboring(stringVal).local
            else -> neighboring(testVal).local
        }
    }

    @Test
    fun `When in single expression`() = forEachPerformanceProfile { profile ->
        val condition = true
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x, profile)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("string"), messageFor0.values.toList())
    }

    @Test
    fun `When in single expression in else case`() = forEachPerformanceProfile { profile ->
        val condition = false
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x, profile)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `When with nested function`() = forEachPerformanceProfile { profile ->
        val condition = true
        val x = if (condition) "hello" else 123
        val result =
            aggregate(0, performanceProfile = profile) {
                fun test() {
                    neighboring("test")
                }

                fun test2() {
                    neighboring("test2")
                }
                when (x) {
                    is String -> test2()
                    else -> test()
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }

    @Test
    fun `Nested when condition must be aligned`() = forEachPerformanceProfile { profile ->
        val condition1 = false
        val condition2 = true
        val result =
            aggregate(0, performanceProfile = profile) {
                when {
                    condition1 -> neighboring("test").local
                    else ->
                        when {
                            condition2 -> neighboring("test2").local
                            else -> neighboring("test3").local
                        }
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }
}
