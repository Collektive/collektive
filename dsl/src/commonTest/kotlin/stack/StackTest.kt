package stack

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue


class StackTest {
    private val token: String = "token"

    @Test
    fun emptyStack() {
        val stack: Stack<Any> = Stack()
        assertTrue(stack.currentPath().path.isEmpty())
    }

    @Test
    fun addTokenToStack() {
        val stack: Stack<Any> = Stack()
        stack.alignRaw(token)
        assertTrue(stack.currentPath().path.contains(token))
    }

    @Test
    fun removeTokenFromStack() {
        val stack: Stack<Any> = Stack()
        stack.alignRaw(token)
        assertTrue(stack.currentPath().path.contains(token))
        stack.dealign()
        assertFalse(stack.currentPath().path.contains(token))
    }

}
