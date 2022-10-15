package aggregateTest

import Environment
import aggregate
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class NeighbouringTest {
    private val notAddedEvent: (String) -> String = { it.uppercase()}
    private val event: (Int) -> Int = { it * 2}
    private val field = Environment.localFields.addField(event)

    @Test
    fun neighbouringSuccessful(){
        aggregate {
            assertNotNull(neighbouring(event))
        }
    }

    @Test
    fun neighbouringFailing(){
        aggregate {
            assertFailsWith<IllegalArgumentException>(
                block = {
                    neighbouring(notAddedEvent)
                }
            )
        }
    }
}