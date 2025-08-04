/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.aggregate

import io.mockk.mockk
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.stdlib.collapse.all
import it.unibo.collektive.stdlib.collapse.none
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FieldsTest {
    private val mockedContext = mockk<Aggregate<Int>>(relaxed = true)
    private val emptyField = Field(mockedContext, 0, 10)
    private val fullField = Field(mockedContext, 0, 1, mapOf(1 to 1, 2 to 2, 3 to 3))

    @Test
    fun `empty fields contain self`() {
        assertTrue(10 in emptyField.includeSelf.values())
        assertFalse(0 in emptyField.includeSelf.values())
        assertTrue(0 in emptyField.includeSelf.ids())
    }

    @Test
    fun `The all operator on an empty field must return true`() {
        assertTrue(emptyField.excludeSelf.all { false })
    }

    @Test
    fun `The all operator including self must return true if the local value matches the predicate`() {
        assertTrue(emptyField.includeSelf.values().all { it == 10 })
    }

    @Test
    fun `The all operator including self must return false if the local value does not matches the predicate`() {
        assertFalse(emptyField.includeSelf.values().all { it == 1 })
    }

    @Test
    fun `The all operator must return true when all the elements in the field match the predicate`() {
        assertTrue(fullField.excludeSelf.values().all { it <= 3 })
    }

    @Test
    fun `The all operator must return false when at least one element in the field does not match the predicate`() {
        assertFalse(fullField.excludeSelf.values().all { it < 2 })
    }

    @Test
    fun `The none operator must return true when applied to a field with no values`() {
        assertTrue(emptyField.excludeSelf.values().none { it == 1 })
    }

    @Test
    fun `The none operator must return true when applied to a field with no values including local value`() {
        assertTrue(emptyField.includeSelf.values().none { it == 1 })
    }

    @Test
    fun `The none operator must return false if at least one element in the field matches the predicate`() {
        assertFalse(fullField.excludeSelf.values().none { it == 2 })
    }
}
