package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathSummary
import it.unibo.collektive.path.impl.IdentityPathSummary

class IfConditionTest : StringSpec({
    val pathRepresentation: (Path) -> PathSummary = { IdentityPathSummary(it) }
    val id0 = 0
    "Branches with constant conditions should get aligned" {
        val result = aggregate(id0, pathRepresentation) {
            if (true) neighboringViaExchange("test")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    "Branches with conditions read from variables should get aligned" {
        val customCondition = true
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition) neighboringViaExchange("test")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    "Function condition if" {
        fun customFunction() = true
        val result = aggregate(id0, pathRepresentation) {
            if (customFunction()) neighboringViaExchange("test")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    "Function and condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition1 && customCondition2) neighboringViaExchange("test")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    "Function or condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition1 || customCondition2) neighboringViaExchange("test")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    "Function not condition if" {
        val customCondition1 = true
        val customCondition2 = false
        val result = aggregate(id0, pathRepresentation) {
            if (customCondition1 && !customCondition2) neighboringViaExchange("test")
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }
})
