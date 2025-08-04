/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

@file:CollektiveIgnore("This test needs to mock the context and does not use projection nor alignment")

package it.unibo.collektive.aggregate

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.CollektiveIgnore
import it.unibo.collektive.stdlib.collapse.fold
import it.unibo.collektive.stdlib.collapse.foldValues
import it.unibo.collektive.stdlib.collapse.reduce
import it.unibo.collektive.stdlib.collapse.reduceValues
import it.unibo.collektive.stdlib.util.replaceMatchingValues
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class FieldOpsTest {
    private val mockedContext = mockk<Aggregate<Int>>().apply {
        every { align(anyNullable()) } just Runs
        every { dealign() } just Runs
        every { alignedOn(anyNullable(), captureLambda<() -> Any?>()) } answers {
            lambda<() -> Any?>().captured.invoke()
        }
    }
    private val emptyField = Field(mockedContext, 0, "localVal")
    private val field = Field(mockedContext, 0, 0, mapOf(1 to 10, 2 to 20))
    private val fulfilledField = Field(mockedContext, 0, 0, mapOf(1 to 10, 2 to 20, 3 to 15))
    private val fulfilledCompatibleField = Field(mockedContext, 0, 1, mapOf(1 to 1, 2 to 1, 3 to 1))

    @Test
    fun `An empty field should return the default value value when is hooded`() {
        assertEquals("default", emptyField.reduceValues { acc, elem -> acc + elem } ?: "default")
    }

    @Test
    fun `A non-empty field should not be hooded on default value`() {
        assertEquals(30, field.reduceValues { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field hooded with Id should return a punctual value related to an evaluation over the id`() {
        assertEquals(
            15,
            fulfilledField.reduce { acc, (id, elem) ->
                acc.mapValue {
                    if (id % 2 == 0) {
                        it + elem
                    } else {
                        it - elem
                    }
                }
            }?.value,
        )
    }

    @Test
    fun `An empty field should return the self value value when is folded`() {
        assertEquals("localVal", emptyField.foldValues(emptyField.local.value) { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field should return the accumulated value when is folded excluding self`() {
        assertEquals(72, field.foldValues(42) { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field folded with Id should return a punctual value related to an evaluation over the id`() {
        assertEquals(
            37,
            fulfilledField.fold(42) { acc: Int, (id: Int, elem: Int) ->
                if (id % 2 ==
                    0
                ) {
                    acc + elem
                } else {
                    acc - elem
                }
            },
        )
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
        assertEquals(sequenceOf(0 to 0, 1 to 10, 2 to 20).toSet(), field.asSequence().map { it.pair }.toSet())
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
        assertEquals("ϕ(localId=0, localValue=localVal, neighbors={})", emptyField.toString())
        assertEquals("ϕ(localId=0, localValue=0, neighbors={1=10, 2=20})", field.toString())
        assertEquals("ϕ(localId=0, localValue=0, neighbors={1=10, 2=20, 3=15})", fulfilledField.toString())
    }
}
