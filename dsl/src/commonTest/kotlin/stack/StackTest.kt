package stack

import kotlin.test.Test
import kotlin.test.assertEquals

class StackTest {
    private val token: String = "token"

    @Test
    fun addTokenToStack() {
        Stack.addToken(token)
        assertEquals(token, Stack.currentPath().path.last())
    }

    @Test
    fun clearStack() {
        Stack.clearStack()
        assertEquals(emptyList(), Stack.currentPath().path)
    }

}
