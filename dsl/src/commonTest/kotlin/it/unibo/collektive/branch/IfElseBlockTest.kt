package it.unibo.collektive.branch

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import kotlin.test.Test
import kotlin.test.assertEquals

class IfElseBlockTest {
    @Test
    fun `true condition in if else should not evaluate else block`() {
        val customCondition = true
        val result =
            aggregate(0) {
                if (customCondition) {
                    neighboringViaExchange("test1")
                } else {
                    neighboringViaExchange("test2")
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test1"), messageFor0.values.toList())
    }

    @Test
    fun `false condition in if else should not evaluate if block`() {
        val customCondition = false
        val result =
            aggregate(0) {
                if (customCondition) {
                    neighboringViaExchange("test1")
                } else {
                    neighboringViaExchange("test2")
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }

    @Test
    fun `if else block should only evaluate when the condition is true`() {
        val customCondition1 = false
        val customCondition2 = true
        val result =
            aggregate(0) {
                if (customCondition1) {
                    neighboringViaExchange("test1")
                } else if (customCondition2) {
                    neighboringViaExchange("test2")
                } else {
                    neighboringViaExchange("test3")
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test2"), messageFor0.values.toList())
    }
}
