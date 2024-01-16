package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.path.Path

class IfElseSingleExpressionTest : StringSpec({
    val id0 = IntId(0)

    "True condition in if else block" {
        val customCondition = true
        val result =
            aggregate(id0) {
                if (customCondition) neighboring("test") else neighboring("test")
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboring.1", "exchange.1")
    }

    "False condition in if else block" {
        val customCondition = false
        val result =
            aggregate(id0) {
                if (customCondition) neighboring("test") else neighboring("test")
            }
        result.toSend.messages.keys shouldContain Path(false, "neighboring.2", "exchange.1")
    }
})
