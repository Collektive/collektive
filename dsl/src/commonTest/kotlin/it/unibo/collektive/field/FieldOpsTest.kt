package it.unibo.collektive.field

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import it.unibo.collektive.field.Field.Companion.fold

class FieldOpsTest : StringSpec({
    val emptyField = Field(0, "localVal")
    val fulfilledField = Field(0, 0, mapOf(1 to 10, 2 to 20))

    "An empty field should return the self value value when is folded" {
        emptyField.fold { acc, elem -> acc + elem } shouldBe "localVal"
    }
    "An empty field should return the initial value when is hooded excluding self" {
        emptyField.fold("initial") { acc, elem -> acc + elem } shouldBe "initial"
    }
    "An empty field when mapped only the local value should be transformed" {
        emptyField.map { "$it-mapped" } shouldBe Field(0, "localVal-mapped", mapOf())
    }
    "A field can be mapped on its values with a given function" {
        fulfilledField.map { it + 3 } shouldBe Field(
            0,
            3,
            mapOf(1 to 13, 2 to 23),
        )
    }
    "A field can be mapped on its values with a given function and the id" {
        fulfilledField.mapWithId { id, value -> "$id-$value" } shouldBe Field(
            0,
            "0-0",
            mapOf(1 to "1-10", 2 to "2-20"),
        )
    }
    "Two fields are equals if they are the same instance" {
        fulfilledField shouldBe fulfilledField
    }
    "Two field are equals if they contains the same values" {
        fulfilledField shouldBe Field(0, 0, mapOf(1 to 10, 2 to 20))
    }
    "Two fields are not equals if they contains different values" {
        fulfilledField shouldNotBeEqual Field(0, 0, mapOf(1 to -1, 2 to -1))
    }
    "Two field are not equals if the contains the same neighboring values but the local id is different" {
        fulfilledField shouldNotBeEqual Field(10, 0, mapOf(1 to 10, 2 to 20))
    }
    "A field should return a sequence containing all the values" {
        fulfilledField.asSequence().toList() shouldContainAll
            sequenceOf(0 to 0, 1 to 10, 2 to 20).toList()
    }
})
