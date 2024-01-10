package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.path.Path

class IfConditionTest : StringSpec({
    val id0 = IntId(0)
    "Constant condition if" {
        val result = aggregate(id0) {
            if (true) neighboring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }

    "Variable condition if" {
        val customCondition = true
        val result = aggregate(id0) {
            if (customCondition) neighboring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }

    "Function condition if" {
        fun customFunction() = true
        val result = aggregate(id0) {
            if (customFunction()) neighboring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }

    "Function and condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(id0) {
            if (customCondition1 && customCondition2) neighboring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }

    "Function or condition if" {
        val customCondition1 = true
        val customCondition2 = true
        val result = aggregate(id0) {
            if (customCondition1 || customCondition2) neighboring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }

    "Function not condition if" {
        val customCondition1 = true
        val customCondition2 = false
        val result = aggregate(id0) {
            if (customCondition1 && !customCondition2) neighboring("test")
        }
        result.toSend.messages.keys shouldContain Path(listOf(true, "neighbouring.1", "exchange.1"))
    }
})
