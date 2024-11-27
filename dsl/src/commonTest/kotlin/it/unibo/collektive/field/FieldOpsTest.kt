package it.unibo.collektive.field

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.foldWithId
import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.Field.Companion.hoodWithId
import it.unibo.collektive.field.operations.replaceMatching

class FieldOpsTest : StringSpec({
    val emptyField = Field(0, "localVal")
    val field = Field(0, 0, mapOf(1 to 10, 2 to 20))
    val fulfilledField = Field(0, 0, mapOf(1 to 10, 2 to 20, 3 to 15))
    val fulfilledCompatibleField = Field(0, 1, mapOf(1 to 1, 2 to 1, 3 to 1))

    "An empty field should return the default value value when is hooded" {
        emptyField.hood("default") { acc, elem -> acc + elem } shouldBe "default"
    }

    "A non-empty field should not be hooded on default value" {
        field.hood(42) { acc, elem -> acc + elem } shouldBe 30
    }

    "A non-empty field hooded with Id should return a punctual value related to an evaluation over the id" {
        fulfilledField.hoodWithId(42) { acc, id, elem -> if (id % 2 == 0) acc + elem else acc - elem } shouldBe 15
    }

    "An empty field should return the self value value when is folded" {
        emptyField.fold(emptyField.localValue) { acc, elem -> acc + elem } shouldBe "localVal"
    }

    "A non-empty field should return the accumulated value when is folded excluding self" {
        field.fold(42) { acc, elem -> acc + elem } shouldBe 72
    }

    "A non-empty field folded with Id should return a punctual value related to an evaluation over the id" {
        fulfilledField.foldWithId(42) { acc, id, elem -> if (id % 2 == 0) acc + elem else acc - elem } shouldBe 37
    }

    "An empty field when mapped only the local value should be transformed" {
        emptyField.map { "$it-mapped" } shouldBe Field(0, "localVal-mapped", mapOf())
    }
    "A field can be mapped on its values with a given function" {
        field.map { it + 3 } shouldBe
            Field(
                0,
                3,
                mapOf(1 to 13, 2 to 23),
            )
    }
    "A field can be mapped on its values with a given function and the id" {
        field.mapWithId { id, value -> "$id-$value" } shouldBe
            Field(
                0,
                "0-0",
                mapOf(1 to "1-10", 2 to "2-20"),
            )
    }
    "Two fields are equals if they are the same instance" {
        field shouldBe field
    }
    "Two field are equals if they contains the same values" {
        field shouldBe Field(0, 0, mapOf(1 to 10, 2 to 20))
    }
    "Two fields are not equals if they contains different values" {
        field shouldNotBeEqual Field(0, 0, mapOf(1 to -1, 2 to -1))
    }
    "Two field are not equals if the contains the same neighboring values but the local id is different" {
        field shouldNotBeEqual Field(10, 0, mapOf(1 to 10, 2 to 20))
    }
    "A field should return a sequence containing all the values" {
        field.asSequence().toList() shouldContainAll
            sequenceOf(0 to 0, 1 to 10, 2 to 20).toList()
    }
    "The replaceMatching on an empty field should return an empty field" {
        emptyField.replaceMatching("replaced") { it == "no-data" } shouldBe emptyField
    }
    "The replaceMatching should return the same field if the predicate is not satisfied" {
        field.replaceMatching(Int.MAX_VALUE) { it == 42 } shouldBe field
    }
    "The replaceMatching should return a field with the replaced values" {
        field.replaceMatching(42) { it == 10 } shouldBe Field(0, 0, mapOf(1 to 42, 2 to 20))
    }
    "An IllegalStateException should be thrown when two fields are not aligned" {
        shouldThrow<IllegalStateException> {
            emptyField.alignedMapWithId(fulfilledField) { _, _, _ -> "no-data" }
        }
    }
    "An empty field should return an empty field when aligned mapped with another empty field" {
        emptyField.alignedMapWithId(emptyField) { _, _, _ -> "no-data" } shouldBe Field(0, "no-data", emptyMap())
    }
    "A field should return a field with the mapped values when aligned mapped with another field" {
        fulfilledField.alignedMapWithId(fulfilledCompatibleField) { _, value, other -> value + other } shouldBe
            Field(
                0,
                1,
                mapOf(1 to 11, 2 to 21, 3 to 16),
            )
    }
    "The string representation of a field should contain the local id and the local value and the neighbors" {
        emptyField.toString() shouldBe "ϕ(localId=0, localValue=localVal, neighbors={})"
        field.toString() shouldBe "ϕ(localId=0, localValue=0, neighbors={1=10, 2=20})"
        fulfilledField.toString() shouldBe "ϕ(localId=0, localValue=0, neighbors={1=10, 2=20, 3=15})"
    }
})
