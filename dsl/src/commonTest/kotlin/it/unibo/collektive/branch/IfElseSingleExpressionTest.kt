package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.operators.neighbouring
import it.unibo.collektive.stack.Path

class IfElseSingleExpressionTest : StringSpec({
    val id0 = IntId(0)

    "True condition in if else block" {
        val customCondition = true
        val result = aggregate(id0) {
            if (customCondition) neighbouring("test") else neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }

    "False condition in if else block" {
        val customCondition = false
        val result = aggregate(id0) {
            if (customCondition) neighbouring("test") else neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(false, "neighbouring.2", "exchange.1"))
    }
})
