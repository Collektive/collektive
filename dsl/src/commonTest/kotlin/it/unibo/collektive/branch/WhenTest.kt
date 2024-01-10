package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.path.Path

class WhenTest : StringSpec({
    val id0 = IntId(0)

    "When in single expression" {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = aggregate(id0) {
            when (x) {
                is String -> neighboring("string")
                else -> neighboring("test")
            }
        }
        result.toSend.messages.keys shouldBe setOf(Path(listOf(true, "neighbouring.1", "exchange.1")))
    }

    "When in single expression in else case" {
        val condition = false
        val x = if (condition) "hello" else 123
        val result = aggregate(id0) {
            when (x) {
                is String -> neighboring("string")
                else -> neighboring("test")
            }
        }
        result.toSend.messages.keys shouldBe setOf(Path(listOf(false, "neighbouring.2", "exchange.1")))
    }

    "When with nested function" {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = aggregate(id0) {
            fun test() {
                neighboring("test")
            }

            fun test2() {
                neighboring("test2")
            }
            when (x) {
                is String -> test2()
                else -> test()
            }
        }
        result.toSend.messages.keys shouldBe setOf(Path(listOf(true, "test2.1", "neighbouring.2", "exchange.1")))
    }
})
