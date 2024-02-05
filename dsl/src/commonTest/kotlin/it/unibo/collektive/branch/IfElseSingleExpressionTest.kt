package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.path.Path

class IfElseSingleExpressionTest : StringSpec({
    val id0 = 0

    "True condition in if else block" {
        val customCondition = true
        val result =
            aggregate(id0) {
                if (customCondition) neighboringViaExchange("test") else neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboringViaExchange.1", "exchanging.1")
    }

    "False condition in if else block" {
        val customCondition = false
        val result =
            aggregate(id0) {
                if (customCondition) neighboringViaExchange("test") else neighboringViaExchange("test")
            }
        result.toSend.messages.keys shouldContain Path(false, "neighboringViaExchange.2", "exchanging.1")
    }
})
