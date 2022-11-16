import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldTest {
    private val myId: ID = 1
    private val myValue: String = "myValue"
    private val connectedId: ID = 2
    private val connectedValue: String = "connectedValue"

    @Test
    fun createFieldWithoutMessages() {
        val field: Field<ID, String> = FieldImpl(Pair(myId, myValue))
        assertTrue(field.toMap().containsKey(myId))
        assertEquals(1, field.toMap().size)
    }

    @Test
    fun createFieldWithMessages() {
        val field: Field<ID, String> = FieldImpl(Pair(myId, myValue), mapOf(connectedId to connectedValue))
        assertTrue(field.toMap().containsKey(myId))
        assertTrue(field.toMap().containsKey(connectedId))
        assertEquals(2, field.toMap().size)
    }

    @Test
    fun getFieldValueById() {
        val field: Field<ID, String> = FieldImpl(Pair(myId, myValue), mapOf(connectedId to connectedValue))
        assertEquals(myValue, field[myId])
        assertEquals(connectedValue, field[connectedId])
    }
}
