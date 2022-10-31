package aggregate

import aggregate
import kotlin.test.Test
import kotlin.test.assertTrue

class NeighbouringTest {
    private val double: (Int) -> Int = { it * 2 }
    private val testValue: Int = 1

    @Test
    fun neighbouringWithoutMessages() {
        aggregate {
            val field = neighbouring(double(testValue))
            assertTrue(field.toMap().containsValue(2))
        }
    }

}