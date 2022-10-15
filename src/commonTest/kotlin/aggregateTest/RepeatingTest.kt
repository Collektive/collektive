package aggregateTest

import Environment.deviceId
import Environment.localFields
import aggregate
import event.EventImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class RepeatingTest {
    private val initialValue: Int = 1
    private val type: (Int) -> Int = { it * 2 }
    private val event = EventImpl(type)

    @Test
    fun repeatingWithInitialValue() {
        aggregate {
            repeating(initialValue, type)
        }
        assertEquals(2, localFields.retrieveField(event).getById(deviceId))
    }

    @Test
    fun repeatingWithLocalValue(){
        aggregate {
            repeating(initialValue, type)
        }
        assertEquals(4, localFields.retrieveField(event).getById(deviceId))
    }
}
