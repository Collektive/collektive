package it.unibo.collektive.branch

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import kotlin.test.Test
import kotlin.test.assertEquals

class WhenTest {
    private fun programUnderTest(input: Any) =
        aggregate(0) {
            when (input) {
                is String -> neighboringViaExchange("string")
                else -> neighboringViaExchange("test")
            }
        }

    @Test
    fun `When in single expression`() {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("string"), messageFor0.values.toList())
    }

    @Test
    fun `When in single expression in else case`() {
        val condition = false
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test"), messageFor0.values.toList())
    }

    @Test
    fun `When with nested function`() {
        val condition = true
        val x = if (condition) "hello" else 123
        val result =
            aggregate(0) {
                fun test() {
                    neighboringViaExchange("test")
                }

                fun test2() {
                    neighboringViaExchange("test2")
                }
                when (x) {
                    is String -> test2()
                    else -> test()
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }

    @Test
    fun `Nested when condition must be aligned`() {
        val condition1 = false
        val condition2 = true
        val result =
            aggregate(0) {
                when {
                    condition1 -> neighboringViaExchange("test")
                    else ->
                        when {
                            condition2 -> neighboringViaExchange("test2")
                            else -> neighboringViaExchange("test3")
                        }
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }
}
