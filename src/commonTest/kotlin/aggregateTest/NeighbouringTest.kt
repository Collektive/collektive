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
    fun pluto(){
        aggregate {
            fun a() = neighbouring(1)
            fun b() = neighbouring(2)
            a()
            b()
        }
        aggregate {
            val field = neighbouring(typeDouble(1))
            assertNotNull(field)
            assertEquals(2, field.getById(deviceId))
        }
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
            println(globalFields)
            neighbouring(typeUppercase("hello"))
            println(globalFields)
            assertEquals(2, globalFields.fields.size)
        }
    }
}
