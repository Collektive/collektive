package it.unibo.collektive.field.operations

import it.unibo.collektive.field.Field
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FieldsTest {
    private val emptyField = Field(0, 10)
    private val fullField = Field(0, 1, mapOf(1 to 1, 2 to 2, 3 to 3))

    @Test
    fun `The all operator on an empty field must return true`() {
        assertTrue(emptyField.all { false })
    }

    @Test
    fun `The all operator including self must return true if the local value matches the predicate`() {
        assertTrue(emptyField.allWithSelf { it == 10 })
    }

    @Test
    fun `The all operator including self must return false if the local value does not matches the predicate`() {
        assertFalse(emptyField.allWithSelf { it == 1 })
    }

    @Test
    fun `The all operator must return true when all the elements in the field match the predicate`() {
        assertTrue(fullField.all { it <= 3 })
    }

    @Test
    fun `The all operator must return false when at least one element in the field does not match the predicate`() {
        assertFalse(fullField.all { it < 2 })
    }

    @Test
    fun `The none operator must return true when applied to a field with no values`() {
        assertTrue(emptyField.none { it == 1 })
    }

    @Test
    fun `The none operator must return true when applied to a field with no values including local value`() {
        assertTrue(emptyField.noneWithSelf { it == 1 })
    }

    @Test
    fun `The none operator must return false if at least one element in the field matches the predicate`() {
        assertFalse(fullField.none { it == 2 })
    }
}
