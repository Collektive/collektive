package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathSummary
import it.unibo.collektive.path.impl.IdentityPathSummary

class IfElseBlockTest : StringSpec({
    val id0 = 0
    val pathRepresentation: (Path) -> PathSummary = { IdentityPathSummary(it) }

    "True condition in if else should not evaluate else block" {
        val customCondition = true
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition) {
                neighboringViaExchange("test1")
            } else {
                neighboringViaExchange("test2")
            }
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test1")
    }

    "False condition in if else should not evaluate if block" {
        val customCondition = false
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition) {
                neighboringViaExchange("test1")
            } else {
                neighboringViaExchange("test2")
            }
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test2")
    }

    "If else block should only evaluate when the condition is true" {
        val customCondition1 = false
        val customCondition2 = true
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition1) {
                neighboringViaExchange("test1")
            } else if (customCondition2) {
                neighboringViaExchange("test2")
            } else {
                neighboringViaExchange("test3")
            }
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test2")
    }
})
