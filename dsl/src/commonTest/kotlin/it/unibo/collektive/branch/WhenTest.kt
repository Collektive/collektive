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

class WhenTest {
    private fun programUnderTest(input: Any) = aggregate(0) {
        when (input) {
            is String -> neighboring("string")
            else -> neighboring("test")
        }
    }

    @Test
    fun `When in single expression`() {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("string"), messageFor0.values.toList())
    }

    @Test
    fun `When in single expression in else case`() {
        val condition = false
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `When with nested function`() {
        val condition = true
        val x = if (condition) "hello" else 123
        val result =
            aggregate(0) {
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
    fun `Nested when condition must be aligned`() {
        val condition1 = false
        val condition2 = true
        val result =
            aggregate(0) {
                when {
                    condition1 -> neighboring("test")
                    else ->
                        when {
                            condition2 -> neighboring("test2")
                            else -> neighboring("test3")
                        }
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }
}
