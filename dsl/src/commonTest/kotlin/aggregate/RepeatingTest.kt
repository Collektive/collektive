package aggregate

import aggregate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepeatingTest {
    private val double: (Int) -> Int = { it * 2 }
    private val initValue: Int = 1

    @Test
    fun firstTimeRepeating() {
        aggregate {
            val res = repeating(initValue, double)
            assertEquals(2, res)
        }
    }

    @Test
    fun moreThanOnceRepeating() {
        var counter = 0
        val condition: () -> Boolean = { counter++ != 2 }
        var res = 0
        aggregate(condition) {
            res = repeating(initValue, double)
        }
        assertEquals(4, res)
    }

    @Test
    fun repeatingWithLambdaBody() {
        val result = aggregate {
            repeating(initValue){
                neighbouring(it*2)
            }
        }
        assertTrue(result.toSend.keys.any {
            it.path.toString().contains("repeating") &&
                    it.path.toString().contains("neighbouring")
        })
    }
}
