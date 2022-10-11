import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AggregateTest {
    private val notAddedEvent: (String) -> String = { it.uppercase()}
    private val event: (Int) -> Int = { it * 2}
    private val field = Environment.fields.addField(event)

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