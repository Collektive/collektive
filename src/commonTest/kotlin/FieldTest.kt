import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class FieldTest {
    private val field: FieldImpl<String> = FieldImpl()
    private val testId: Int = 0
    private val wrongId: Int = 42
    private val testValue: String = "test"

    @Test
    fun emptyField(){
        assertEquals(emptyMap(), FieldImpl<String>().field)
    }
    @Test
    fun addElementToField(){
        field.addElement(testId, testValue)
        assertTrue(field.field.containsValue(testValue))
    }
    @Test
    fun getValueById(){
        field.addElement(testId, testValue)
        assertEquals(testValue, field.getById(testId))
    }

    @Test
    fun getValueByWrongId(){
        field.addElement(testId, testValue)
        assertFailsWith<IllegalArgumentException>(
            block = {
                field.getById(wrongId)
            }
        )
    }
}
