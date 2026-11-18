/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.branch

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.testing.forEachPerformanceProfile
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class IfConditionTest {
    @Test
    fun `Branches with constant conditions should get aligned`() = forEachPerformanceProfile { profile ->
        val result = aggregate(0, performanceProfile = profile) {
            if (true) neighboring("test").local
        }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Branches with conditions read from variables should get aligned`() = forEachPerformanceProfile { profile ->
        val customCondition = true
        val result = aggregate(0, performanceProfile = profile) {
            if (customCondition) neighboring("test").local
        }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function condition if`() = forEachPerformanceProfile { profile ->
        fun customFunction() = Random.nextDouble() > -1
        val result = aggregate(0, performanceProfile = profile) {
            if (customFunction()) neighboring("test").local
        }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function and condition if`() = forEachPerformanceProfile { profile ->
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(0, performanceProfile = profile) {
            if (customCondition1 && customCondition2) neighboring("test").local
        }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function or condition if`() = forEachPerformanceProfile { profile ->
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(0, performanceProfile = profile) {
            if (customCondition1 || customCondition2) neighboring("test").local
        }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function not condition if`() = forEachPerformanceProfile { profile ->
        val customCondition1 = true
        val customCondition2 = false
        val result = aggregate(0, performanceProfile = profile) {
            if (customCondition1 && !customCondition2) neighboring("test").local
        }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }
}
