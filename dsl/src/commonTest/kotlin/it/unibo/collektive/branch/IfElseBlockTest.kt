package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.aggregate
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.stack.Path

class IfElseBlockTest : StringSpec({
    val id0 = IntId(0)

    "True condition in if else should not evaluate else block" {
        val customCondition = true
        val result = aggregate(id0) {
            if (customCondition) {
                neighbouring("test1")
            } else {
                neighbouring("test2")
            }
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[customCondition, true]",
                "neighbouring.1",
                "exchange.1",
            ),
        )
    }

    "False condition in if else should not evaluate if block" {
        val customCondition = false
        val result = aggregate(id0) {
            if (customCondition) {
                neighbouring("test1")
            } else {
                neighbouring("test2")
            }
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[constant, false]",
                "neighbouring.2",
                "exchange.1",
            ),
        )
    }

    "If else block should only evaluate when the condition is true" {
        val customCondition1 = false
        val customCondition2 = true
        val result = aggregate(id0) {
            if (customCondition1) {
                neighbouring("test1")
            } else if (customCondition2) {
                neighbouring("test2")
            } else {
                neighbouring("test3")
            }
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[customCondition2, true]",
                "neighbouring.2",
                "exchange.1",
            ),
        )
    }
})
