import kotlin.test.Test
import kotlin.test.assertEquals

class FieldTest {
    @Test
    fun emptyField(){
        assertEquals(FieldImpl<String>().field, emptyMap())
    }
}
