package it.unibo.collektive.branch

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import kotlin.test.Test
import kotlin.test.assertEquals

class IfElseSingleExpressionTest {
    @Test
    fun `true condition in if else block`() {
        val customCondition = true
        val result =
            aggregate(0) {
                if (customCondition) neighboringViaExchange("test-true") else neighboringViaExchange("test-false")
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test-true"), messageFor0.values.toList())
    }

    @Test
    fun `false condition in if else block`() {
        val customCondition = false
        val result =
            aggregate(0) {
                if (customCondition) neighboringViaExchange("test-true") else neighboringViaExchange("test-false")
            }
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(1, messageFor0.size)
        assertEquals(listOf("test-false"), messageFor0.values.toList())
    }
}
