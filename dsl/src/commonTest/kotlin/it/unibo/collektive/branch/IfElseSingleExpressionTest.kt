package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange

class IfElseSingleExpressionTest : StringSpec({
    val id0 = 0

    "True condition in if else block" {
        val customCondition = true
        val result = aggregate(id0) {
            if (customCondition) neighboringViaExchange("test-true") else neighboringViaExchange("test-false")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test-true")
    }

    "False condition in if else block" {
        val customCondition = false
        val result = aggregate(id0) {
            if (customCondition) neighboringViaExchange("test-true") else neighboringViaExchange("test-false")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test-false")
    }
})
