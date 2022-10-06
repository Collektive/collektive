import kotlin.test.Test
import kotlin.test.assertEquals

class FieldsTest {
    @Test
    fun addField(){
        assertEquals(Fields.addField(FieldImpl<Double>()), true)
    }
}
