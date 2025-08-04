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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldTest {
    private val mockedContext = mockk<Aggregate<Int>>(relaxed = true)
    private val myId = 0
    private val myValue: String = "myValue"
    private val connectedId = 1
    private val connectedValue: String = "connectedValue"

    @Test
    fun createFieldWithoutMessages() {
        val field: Field<Int, String> = Field(mockedContext, myId, myValue)
        assertTrue(field.includeSelf.toMap().containsKey(myId))
        assertEquals(1, field.includeSelf.toMap().size)
    }

    @Test
    fun createFieldWithMessages() {
        val field: Field<Int, String> = Field(mockedContext, myId, myValue, mapOf(connectedId to connectedValue))
        assertTrue(field.includeSelf.toMap().containsKey(myId))
        assertTrue(field.includeSelf.toMap().containsKey(connectedId))
        assertEquals(2, field.includeSelf.toMap().size)
    }

    @Test
    fun getFieldValueById() {
        val field: Field<Int, String> = Field(mockedContext, myId, myValue, mapOf(connectedId to connectedValue))
        assertEquals(myValue, field[myId])
        assertEquals(connectedValue, field[connectedId])
    }
}
