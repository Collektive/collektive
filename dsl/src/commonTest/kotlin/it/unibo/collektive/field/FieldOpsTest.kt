package it.unibo.collektive.field

import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.equals.shouldNotBeEqual
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.Field.Companion.hoodInto
import it.unibo.collektive.field.Field.Companion.reduce

class FieldOpsTest : StringSpec({
    val emptyField = Field(IntId(0), "localVal")
    val fulfilledField = Field(IntId(0), 0, mapOf(IntId(1) to 10, IntId(2) to 20))

    "An empty field should raise an exception when is reduced" {
        shouldThrowUnit<UnsupportedOperationException> {
            emptyField.reduce(includingSelf = false) { acc, elem -> acc + elem }
        }
    }
    "An empty field should return the self value value when is hooded" {
        emptyField.hood { acc, elem -> acc + elem } shouldBe "localVal"
    }
    "An empty field should return the initial value when is hooded excluding self" {
        emptyField.hoodInto("initial") { acc, elem -> acc + elem } shouldBe "initial"
    }
    "An empty field when mapped only the local value should be transformed" {
        emptyField.map { "$it-mapped" } shouldBe Field(IntId(0), "localVal-mapped", mapOf())
    }
    "A field can be mapped on its values with a given function" {
        fulfilledField.map { it + 3 } shouldBe Field(
            IntId(0),
            3,
            mapOf(IntId(1) to 13, IntId(2) to 23),
        )
    }
    "A field can be mapped on its values with a given function and the id" {
        fulfilledField.mapWithId { id, value -> "${(id as IntId).id}-$value" } shouldBe Field(
            IntId(0),
            "0-0",
            mapOf(IntId(1) to "1-10", IntId(2) to "2-20"),
        )
    }
    "Two fields are equals if they are the same instance" {
        fulfilledField shouldBe fulfilledField
    }
    "Two field are equals if they contains the same values" {
        fulfilledField shouldBe Field(IntId(0), 0, mapOf(IntId(1) to 10, IntId(2) to 20))
    }
    "Two fields are not equals if they contains different values" {
        fulfilledField shouldNotBeEqual Field(IntId(0), 0, mapOf(IntId(1) to -1, IntId(2) to -1))
    }
    "Two field are not equals if the contains the same neighbouring values but the local id is different" {
        fulfilledField shouldNotBeEqual Field(IntId(10), 0, mapOf(IntId(1) to 10, IntId(2) to 20))
    }
    "A field should return a sequence containing all the values" {
        fulfilledField.asSequence().toList() shouldContainAll
            sequenceOf(IntId(0) to 0, IntId(1) to 10, IntId(2) to 20).toList()
    }
})
