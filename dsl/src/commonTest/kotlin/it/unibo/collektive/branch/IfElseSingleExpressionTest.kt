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

class IfElseSingleExpressionTest {
    @Test
    fun `true condition in if else block`() {
        val customCondition = true
        val result =
            aggregate(0) {
                if (customCondition) neighboring("test-true") else neighboring("test-false")
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test-true"), messageFor0.values.toList())
    }

    @Test
    fun `false condition in if else block`() {
        val customCondition = false
        val result =
            aggregate(0) {
                if (customCondition) neighboring("test-true") else neighboring("test-false")
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test-false"), messageFor0.values.toList())
    }
}
