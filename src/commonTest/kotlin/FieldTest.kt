import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FieldTest {
    @Test
    fun emptyField(){
        assertEquals(FieldImpl<String>().field, emptyMap())
    }
    @Test
    fun addElementToField(){
        val field = FieldImpl<String>()
        field.addElement(0, "test")
        assertTrue(field.field.containsValue("test"))
    }
}
