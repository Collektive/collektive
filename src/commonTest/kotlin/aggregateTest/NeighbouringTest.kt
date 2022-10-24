package aggregateTest

import Environment.deviceId
import Environment.globalFields
import aggregate
import kotlin.test.*

class NeighbouringTest {
    private val typeDouble: (Int) -> Int = { it * 2 }
    private val typeUppercase: (String) -> String = { it.uppercase() }

    @BeforeTest
    fun resetGlobalFields(){
        globalFields.fields.clear()
    }

    @Test
    fun neighbouringSuccessful(){
        aggregate {
            val field = neighbouring(typeDouble(1))
            assertNotNull(field)
            assertEquals(2, field.getById(deviceId))
        }
    }

    @Test
    fun neighbouringWithDifferentEvent(){
        aggregate {
            neighbouring(typeDouble(1))
            neighbouring(typeUppercase("hello"))
            assertEquals(2, globalFields.fields.size)
        }
    }
}
