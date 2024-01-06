package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.path.Path

class IfElseBlockTest : StringSpec({
    val id0 = IntId(0)

    "True condition in if else should not evaluate else block" {
        val customCondition = true
        val result =
            aggregate(id0) {
                if (customCondition) {
                    neighboring("test1")
                } else {
                    neighboring("test2")
                }
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboring.1", "exchange.1")
    }

    "False condition in if else should not evaluate if block" {
        val customCondition = false
        val result =
            aggregate(id0) {
                if (customCondition) {
                    neighboring("test1")
                } else {
                    neighboring("test2")
                }
            }
        result.toSend.messages.keys shouldContain Path(false, "neighboring.2", "exchange.1")
    }

    "If else block should only evaluate when the condition is true" {
        val customCondition1 = false
        val customCondition2 = true
        val result =
            aggregate(id0) {
                if (customCondition1) {
                    neighboring("test1")
                } else if (customCondition2) {
                    neighboring("test2")
                } else {
                    neighboring("test3")
                }
            }
        result.toSend.messages.keys shouldContain Path(true, "neighboring.2", "exchange.1")
    }
})
