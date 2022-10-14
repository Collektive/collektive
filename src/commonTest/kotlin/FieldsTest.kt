import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FieldsTest {
    private val fields = Fields<Any>()
    @Test
    fun addField(){
        fields.addField(Int)
        assertEquals(1, fields.retrieveAllFields().size)
    }

    @Test
    fun addFieldOFDifferentType(){
        fields.addField(Int)
        fields.addField(String)
        assertEquals(2, fields.retrieveAllFields().size)
    }

    @Test
    fun retrieveField() {
        fields.addField(Int)
        assertNotNull(fields.retrieveField(Int))
    }

    @Test
    fun modifyField() {
        fields.addField(String)
        fields.retrieveField(String).addElement(0, "test")
        assertNotNull(fields.retrieveField(String).getById(0))
    }
}
