package aggregateTest

import Environment.deviceId
import Environment.localFields
import aggregate
import kotlin.test.Test
import kotlin.test.assertEquals

class RepeatingTest {
    private val initialValue: Int = 1
    private val event: (Int) -> Int = { it * 2}

    @Test
    fun repeatingWithInitialValue(){
        aggregate {
            repeating(initialValue, event)
        }
        assertEquals(2, localFields.retrieveField(event).getById(deviceId))
    }

    @Test
    fun repeatingWithLocalValue(){
        aggregate {
            repeating(initialValue, event)
        }
        assertEquals(4, localFields.retrieveField(event).getById(deviceId))
    }
}