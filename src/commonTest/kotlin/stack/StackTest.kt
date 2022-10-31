package stack

import kotlin.test.Test
import kotlin.test.assertEquals

class StackTest {
    private val stack: Stack = StackImpl()
    private val double: (Int) -> Int = { it * 2 }
    private val initValue: Int = 1


    @Test
    fun openNewFrame() {
        val res = stack.inNewFrame(Token.REPEATING) {
            assertEquals(Token.REPEATING.toString(), it.currentList().last())
            double(initValue)
        }
        assertEquals(2, res)
    }
}
