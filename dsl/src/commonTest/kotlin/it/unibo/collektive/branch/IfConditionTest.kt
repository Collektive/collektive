package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.stack.Path

class IfConditionTest : StringSpec({
    val id0 = IntId(0)
    "Constant condition if" {
        val result = aggregate(id0) {
            if (true) neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf("branch[constant, true]", "neighbouring.1", "exchange.1"))
    }

    "Variable condition if" {
        val customCondition = true
        val result = aggregate(id0) {
            if (customCondition) neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[customCondition, true]",
                "neighbouring.1",
                "exchange.1",
            ),
        )
    }

    "Function condition if" {
        fun customFunction() = true
        val result = aggregate(id0) {
            if (customFunction()) neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[customFunction, true]",
                "neighbouring.1",
                "exchange.1",
            ),
        )
    }

    "Function and condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(id0) {
            if (customCondition1 && customCondition2) neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(
            listOf("branch[customCondition1 & customCondition2, true]", "neighbouring.1", "exchange.1"),
        )
    }

    "Function or condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(id0) {
            if (customCondition1 || customCondition2) neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[customCondition1 | customCondition2, true]",
                "neighbouring.1",
                "exchange.1",
            ),
        )
    }

    "Function not condition if" {
        val customCondition1 = true
        val customCondition2 = false
        val result = aggregate(id0) {
            if (customCondition1 && !customCondition2) neighbouring("test")
        }
        result.toSend.messages.keys shouldContain Path(
            listOf(
                "branch[customCondition1 & not customCondition2, true]",
                "neighbouring.1",
                "exchange.1",
            ),
        )
    }
})
