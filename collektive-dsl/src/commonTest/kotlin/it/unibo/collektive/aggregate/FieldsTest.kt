/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import it.unibo.collektive.stdlib.fields.all
import it.unibo.collektive.stdlib.fields.allValues
import it.unibo.collektive.stdlib.fields.containsValue
import it.unibo.collektive.stdlib.fields.noValue
import it.unibo.collektive.stdlib.util.IncludingSelf
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FieldsTest {
    private val emptyField = Field(0, 10)
    private val fullField = Field(0, 1, mapOf(1 to 1, 2 to 2, 3 to 3))
    private val charField = Field(0, 'a', mapOf(1 to 'b', 2 to 'c', 3 to 'd'))

    @Test
    fun `empty fields contain self`() {
        assertTrue(emptyField.containsValue(10))
        assertFalse(emptyField.containsValue(0))
        assertTrue(emptyField.containsId(0))
    }

    @Test
    fun `The all operator on an empty field must return true`() {
        assertTrue(emptyField.all { false })
    }

    @Test
    fun `The all operator including self must return true if the local value matches the predicate`() {
        assertTrue(emptyField.allValues(IncludingSelf) { it == 10 })
    }

    @Test
    fun `The all operator including self must return false if the local value does not matches the predicate`() {
        assertFalse(emptyField.allValues(IncludingSelf) { it == 1 })
    }

    @Test
    fun `The all operator must return true when all the elements in the field match the predicate`() {
        assertTrue(fullField.allValues { it <= 3 })
    }

    @Test
    fun `The all operator must return false when at least one element in the field does not match the predicate`() {
        assertFalse(fullField.allValues { it < 2 })
    }

    @Test
    fun `The none operator must return true when applied to a field with no values`() {
        assertTrue(emptyField.noValue { it == 1 })
    }

    @Test
    fun `The none operator must return true when applied to a field with no values including local value`() {
        assertTrue(emptyField.noValue(IncludingSelf) { it == 1 })
    }

    @Test
    fun `The none operator must return false if at least one element in the field matches the predicate`() {
        assertFalse(fullField.noValue { it == 2 })
    }
}
