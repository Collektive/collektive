package it.unibo.collektive.field.operations

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.field.Field

class FieldsTest : FreeSpec({
    val emptyField = Field(0, 10)
    val fullField = Field(0, 1, mapOf(1 to 1, 2 to 2, 3 to 3))
    "The all operator on an empty field must return true" {
        emptyField.all { false } shouldBe true
    }
    "The all operator including self must return true if the local value matches the predicate" {
        emptyField.allWithSelf { it == 10 } shouldBe true
    }
    "The all operator including self must return false if the local value does not matches the predicate" {
        emptyField.allWithSelf { it == 1 } shouldBe false
    }
    "The all operator must return true when all the elements in the field match the predicate" {
        fullField.all { it <= 3 } shouldBe true
    }
    "The all operator must return false when at least one element in the field does not match the predicate" {
        fullField.all { it < 2 } shouldBe false
    }
    "The none operator must return true when applied to a field with no values" {
        emptyField.none { it == 1 } shouldBe true
    }
    "The none operator must return true when applied to a field with no values including local value" {
        emptyField.noneWithSelf { it == 1 } shouldBe true
    }
    "The none operator must return false if at least one element in the field matches the predicate" {
        fullField.none { it == 2 } shouldBe false
    }
})
