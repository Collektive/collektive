package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.path.Path

class IfConditionTest : StringSpec({
    val id0 = 0
    "Branches with constant conditions should get aligned" {
        val result =
            aggregate(id0) {
                if (true) neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchange.1")
    }

    "Branches with conditions read from variables should get aligned" {
        val customCondition = true
        val result =
            aggregate(id0) {
                if (customCondition) neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchange.1")
    }

    "Function condition if" {
        fun customFunction() = true
        val result =
            aggregate(id0) {
                if (customFunction()) neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchange.1")
    }

    "Function and condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result =
            aggregate(id0) {
                if (customCondition1 && customCondition2) neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchange.1")
    }

    "Function or condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result =
            aggregate(id0) {
                if (customCondition1 || customCondition2) neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchange.1")
    }

    "Function not condition if" {
        val customCondition1 = true
        val customCondition2 = false
        val result =
            aggregate(id0) {
                if (customCondition1 && !customCondition2) neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchange.1")
    }
})
