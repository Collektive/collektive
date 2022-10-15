import event.EventImpl
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class FieldsTest {
    private val fields = LocalFields<Any>()
    @Test
    fun addField(){
        fields.addField(EventImpl(Int))
        assertEquals(1, fields.retrieveAllFields().size)
    }

    @Test
    fun addFieldOFDifferentType(){
        fields.addField(EventImpl(Int))
        fields.addField(EventImpl(String))
        assertEquals(2, fields.retrieveAllFields().size)
    }

    @Test
    fun retrieveField() {
        fields.addField(EventImpl(Int))
        assertNotNull(fields.retrieveField(EventImpl(Int)))
    }

    @Test
    fun modifyField() {
        fields.addField(EventImpl(String))
        fields.retrieveField(EventImpl(String)).addElement(0, "test")
        assertNotNull(fields.retrieveField(EventImpl(String)).getById(0))
    }
}
