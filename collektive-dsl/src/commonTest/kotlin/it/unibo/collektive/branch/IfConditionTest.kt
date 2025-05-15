/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.branch

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.neighboring
import kotlin.test.Test
import kotlin.test.assertEquals

class IfConditionTest {
    @Test
    fun `Branches with constant conditions should get aligned`() {
        val result =
            aggregate(0) {
                if (true) neighboring("test").local
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Branches with conditions read from variables should get aligned`() {
        val customCondition = true
        val result =
            aggregate(0) {
                if (customCondition) neighboring("test").local
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function condition if`() {
        fun customFunction() = true
        val result =
            aggregate(0) {
                if (customFunction()) neighboring("test").local
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function and condition if`() {
        val customCondition1 = true
        val customCondition2 = true
        val result =
            aggregate(0) {
                if (customCondition1 && customCondition2) neighboring("test").local
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function or condition if`() {
        val customCondition1 = true
        val customCondition2 = true
        val result =
            aggregate(0) {
                if (customCondition1 || customCondition2) neighboring("test").local
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `Function not condition if`() {
        val customCondition1 = true
        val customCondition2 = false
        val result =
            aggregate(0) {
                if (customCondition1 && !customCondition2) neighboring("test").local
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }
}
