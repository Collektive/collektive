package it.unibo.collektive.field

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import it.unibo.collektive.field.Field.Companion.fold
import it.unibo.collektive.field.Field.Companion.foldWithId
import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.Field.Companion.hoodWithId

class FieldOpsTest : StringSpec({
    val emptyField = Field(0, "localVal")
    val field = Field(0, 0, mapOf(1 to 10, 2 to 20))
    val fulfilledField = Field(0, 0, mapOf(1 to 10, 2 to 20, 3 to 15))

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
        field.map { it + 3 } shouldBe Field(
            0,
            3,
            mapOf(1 to 13, 2 to 23),
        )
    }
    "A field can be mapped on its values with a given function and the id" {
        field.mapWithId { id, value -> "$id-$value" } shouldBe Field(
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
})
