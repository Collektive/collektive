/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.field

import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.foldWithId
import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.Field.Companion.hoodWithId
import it.unibo.collektive.field.operations.replaceMatching
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse

class FieldOpsTest {
    private val emptyField = Field(0, "localVal")
    private val field = Field(0, 0, mapOf(1 to 10, 2 to 20))
    private val fulfilledField = Field(0, 0, mapOf(1 to 10, 2 to 20, 3 to 15))
    private val fulfilledCompatibleField = Field(0, 1, mapOf(1 to 1, 2 to 1, 3 to 1))

    @Test
    fun `An empty field should return the default value value when is hooded`() {
        assertEquals("default", emptyField.hood("default") { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field should not be hooded on default value`() {
        assertEquals(30, field.hood(42) { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field hooded with Id should return a punctual value related to an evaluation over the id`() {
        assertEquals(15, fulfilledField.hoodWithId(42) { acc, id, elem -> if (id % 2 == 0) acc + elem else acc - elem })
    }

    @Test
    fun `An empty field should return the self value value when is folded`() {
        assertEquals("localVal", emptyField.fold(emptyField.localValue) { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field should return the accumulated value when is folded excluding self`() {
        assertEquals(72, field.fold(42) { acc, elem -> acc + elem })
    }

    @Test
    fun `A non-empty field folded with Id should return a punctual value related to an evaluation over the id`() {
        assertEquals(37, fulfilledField.foldWithId(42) { acc, id, elem -> if (id % 2 == 0) acc + elem else acc - elem })
    }

    @Test
    fun `An empty field when mapped only the local value should be transformed`() {
        assertEquals(Field(0, "localVal-mapped", mapOf()), emptyField.map { "$it-mapped" })
    }

    @Test
    fun `A field can be mapped on its values with a given function`() {
        assertEquals(Field(0, 3, mapOf(1 to 13, 2 to 23)), field.map { it + 3 })
    }

    @Test
    fun `A field can be mapped on its values with a given function and the id`() {
        assertEquals(Field(0, "0-0", mapOf(1 to "1-10", 2 to "2-20")), field.mapWithId { id, value -> "$id-$value" })
    }

    @Test
    fun `Two fields are equals if they are the same instance`() {
        assertEquals(field, field)
    }

    @Test
    fun `Two field are equals if they contains the same values`() {
        assertEquals(Field(0, 0, mapOf(1 to 10, 2 to 20)), field)
    }

    @Test
    fun `Two fields are not equals if they contains different values`() {
        assertFalse { field == Field(0, 0, mapOf(1 to -1, 2 to -1)) }
    }

    @Test
    fun `Two field are not equals if the contains the same neighboring values but the local id is different`() {
        assertFalse { field == Field(10, 0, mapOf(1 to 10, 2 to 20)) }
    }

    @Test
    fun `A field should return a sequence containing all the values`() {
        assertEquals(sequenceOf(0 to 0, 1 to 10, 2 to 20).toSet(), field.asSequence().toSet())
    }

    @Test
    fun `The replaceMatching on an empty field should return an empty field`() {
        assertEquals(emptyField, emptyField.replaceMatching("replaced") { it == "no-data" })
    }

    @Test
    fun `The replaceMatching should return the same field if the predicate is not satisfied`() {
        assertEquals(field, field.replaceMatching(Int.MAX_VALUE) { it == 42 })
    }

    @Test
    fun `The replaceMatching should return a field with the replaced values`() {
        assertEquals(Field(0, 0, mapOf(1 to 42, 2 to 20)), field.replaceMatching(42) { it == 10 })
    }

    @Test
    fun `An IllegalStateException should be thrown when two fields are not aligned`() {
        assertFailsWith<IllegalStateException> {
            emptyField.alignedMapWithId(fulfilledField) { _, _, _ -> "no-data" }
        }
    }

    @Test
    fun `An empty field should return an empty field when aligned mapped with another empty field`() {
        assertEquals(Field(0, "no-data", emptyMap()), emptyField.alignedMapWithId(emptyField) { _, _, _ -> "no-data" })
    }

    @Test
    fun `A field should return a field with the mapped values when aligned mapped with another field`() {
        assertEquals(
            Field(0, 1, mapOf(1 to 11, 2 to 21, 3 to 16)),
            fulfilledField.alignedMapWithId(fulfilledCompatibleField) { _, value, other -> value + other },
        )
    }

    @Test
    fun `The string representation of a field should contain the local id and the local value and the neighbors`() {
        assertEquals("ϕ(localId=0, localValue=localVal, neighbors={})", emptyField.toString())
        assertEquals("ϕ(localId=0, localValue=0, neighbors={1=10, 2=20})", field.toString())
        assertEquals("ϕ(localId=0, localValue=0, neighbors={1=10, 2=20, 3=15})", fulfilledField.toString())
    }
}
