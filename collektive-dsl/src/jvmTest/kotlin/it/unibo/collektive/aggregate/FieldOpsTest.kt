/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:CollektiveIgnore("This test needs to mock the context and does not use projection nor alignment")

package it.unibo.collektive.aggregate

import arrow.core.none
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.stdlib.collapse.fold
import it.unibo.collektive.stdlib.collapse.reduce
import it.unibo.collektive.stdlib.collapse.sum
import it.unibo.collektive.stdlib.util.replaceMatchingValues
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNull

class FieldOpsTest {
    private val mockedContext = mockk<Aggregate<Int>>().apply {
        every { align(anyNullable()) } just Runs
        every { dealign() } just Runs
        every { alignedOn(anyNullable(), captureLambda<() -> Any?>()) } answers {
            lambda<() -> Any?>().captured.invoke()
        }
    }
    private val emptyField = Field(mockedContext, 0, "localVal")
    private val emptyFieldOfNullables: Field<Int, String?> = emptyField
    private val field = Field(mockedContext, 0, 0, mapOf(1 to 10, 2 to 20))
    private val fulfilledField = Field(mockedContext, 0, 0, mapOf(1 to 10, 2 to 20, 3 to 15))
    private val fulfilledCompatibleField = Field(mockedContext, 0, 1, mapOf(1 to 1, 2 to 1, 3 to 1))

    @Test
    fun `An empty field should return null or an empty Option when its peer values are reduced`() {
        assertNull(emptyField.neighbors.values.reduce { acc, elem -> acc + elem })
        assertEquals(none(), emptyFieldOfNullables.neighbors.values.reduce { acc, elem -> acc + elem })
    }

    @Test
    fun `An empty field should return the local value when its values are reduced including self`() {
        assertEquals("localVal", emptyField.all.values.reduce { acc, elem -> acc + elem })
    }

    @Test
    fun `Reduction on peers should exclude the local value`() {
        assertEquals(30, field.neighbors.values.sum())
    }

    @Test
    fun `Reducing using ids can work as a filter`() {
        assertEquals(
            15,
            fulfilledField.neighbors.reduce { first, (id, value) ->
                first.mapValue { if (id % 2 == 0) it + value else it - value }
            }?.value,
        )
    }

    @Test
    fun `An empty field should return the self value value when reduced including self`() {
        assertEquals("localVal", emptyField.all.values.reduce(String::plus))
    }

    @Test
    fun `A non-empty field should return the accumulated value when is folded excluding self`() {
        assertEquals(72, field.neighbors.values.fold(42) { acc, elem -> acc + elem })
    }

    @Test
    fun `An empty field when mapped only the local value should be transformed`() {
        assertEquals(Field(mockedContext, 0, "localVal-mapped", mapOf()), emptyField.mapValues { "$it-mapped" })
    }

    @Test
    fun `A field can be mapped on its values with a given function`() {
        assertEquals(Field(mockedContext, 0, 3, mapOf(1 to 13, 2 to 23)), field.mapValues { it + 3 })
    }

    @Test
    fun `A field can be mapped on its values with a given function and the id`() {
        assertEquals(
            Field(mockedContext, 0, "0-0", mapOf(1 to "1-10", 2 to "2-20")),
            field.map { (id, value) ->
                "$id-$value"
            },
        )
    }

    @Test
    fun `Two fields are equals if they are the same instance`() {
        assertEquals(field, field)
    }

    @Test
    fun `Two field are equals if they contains the same values`() {
        assertEquals(Field(mockedContext, 0, 0, mapOf(1 to 10, 2 to 20)), field)
    }

    @Test
    fun `Two fields are not equals if they contains different values`() {
        assertNotEquals(field, Field(mockedContext, 0, 0, mapOf(1 to -1, 2 to -1)))
    }

    @Test
    fun `Two field are not equals if the contains the same neighboring values but the local id is different`() {
        assertNotEquals(field, Field(mockedContext, 10, 0, mapOf(1 to 10, 2 to 20)))
    }

    @Test
    fun `A field should return a sequence containing all the values`() {
        assertEquals(sequenceOf(0 to 0, 1 to 10, 2 to 20).toSet(), field.all.sequence.map { it.pair }.toSet())
    }

    @Test
    fun `The replaceMatching on an empty field should return an empty field`() {
        assertEquals(emptyField, emptyField.replaceMatchingValues("replaced") { it == "no-data" })
    }

    @Test
    fun `The replaceMatching should return the same field if the predicate is not satisfied`() {
        assertEquals(field, field.replaceMatchingValues(Int.MAX_VALUE) { it == 42 })
    }

    @Test
    fun `The replaceMatching should return a field with the replaced values`() {
        assertEquals(Field(mockedContext, 0, 0, mapOf(1 to 42, 2 to 20)), field.replaceMatchingValues(42) { it == 10 })
    }

    @Test
    fun `An empty field should return an empty field when aligned mapped with another empty field`() {
        assertEquals(
            Field(mockedContext, 0, "no-data", emptyMap()),
            emptyField.alignedMap(emptyField) { _, _, _ ->
                "no-data"
            },
        )
    }

    @Test
    fun `A field should return a field with the mapped values when aligned mapped with another field`() {
        assertEquals(
            Field(mockedContext, 0, 1, mapOf(1 to 11, 2 to 21, 3 to 16)),
            fulfilledField.alignedMap(fulfilledCompatibleField) { _, value, other -> value + other },
        )
    }

    @Test
    fun `The string representation of a field should contain the local id and the local value and the neighbors`() {
        assertEquals("ϕ(pointwise: 0=localVal)", emptyField.toString())
        assertEquals("ϕ(local: 0=0, neighbors: {1=10, 2=20})", field.toString())
        assertEquals("ϕ(local: 0=0, neighbors: {1=10, 2=20, 3=15})", fulfilledField.toString())
    }
}
