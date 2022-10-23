package aggregateTest

import Environment.localFields
import aggregate
import event.EventImpl
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class NeighbouringTest {
    private val notAddedType: (String) -> String = { it.uppercase()}
    private val type: (Int) -> Int = { it * 2}
    private val event = EventImpl(type)
    private val field = localFields.addField(event)

    @Test
    fun neighbouringSuccessful(){
        aggregate {
            assertNotNull(neighbouring(type))
        }
    }

    @Test
    fun neighbouringFailing(){
        aggregate {
            assertFailsWith<IllegalArgumentException>(
                block = {
                    neighbouring(notAddedType)
                }
            )
        }
    }
}
