package it.unibo.collektive.field

import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.StringSpec
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field.Companion.reduce

class FieldOpsTest : StringSpec({
    "An empty field should raise an exception when is reduced" {
        val field = Field(IntId(0), 0)
        shouldThrowUnit<UnsupportedOperationException> {
            field.reduce(includingSelf = false) { acc, elem -> acc + elem }
        }
    }
})
