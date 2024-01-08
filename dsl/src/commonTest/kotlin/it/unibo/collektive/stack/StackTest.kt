package it.unibo.collektive.stack

import it.unibo.collektive.aggregate.api.impl.stack.Stack
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class StackTest {
    private val token: String = "token"

    @Test
    fun emptyStack() {
        val stack = Stack()
        assertTrue(stack.currentPath().tokens().isEmpty())
    }

    @Test
    fun addTokenToStack() {
        val stack = Stack()
        stack.alignRaw(token)
        assertTrue(stack.currentPath().tokens().contains(token))
    }

    @Test
    fun removeTokenFromStack() {
        val stack = Stack()
        stack.alignRaw(token)
        assertTrue(stack.currentPath().tokens().contains(token))
        stack.dealign()
        assertFalse(stack.currentPath().tokens().contains(token))
    }
}
