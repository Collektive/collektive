package it.unibo.collektive.branch

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathSummary
import it.unibo.collektive.path.impl.IdentityPathSummary

class WhenTest : StringSpec({
    val id0 = 0
    val pathRepresentation: (Path) -> PathSummary = { IdentityPathSummary(it) }

    "When in single expression" {
        val condition = true
        val x = if (condition) "hello" else 123
        val result =
            aggregate(id0, pathRepresentation) {
                when (x) {
                    is String -> neighboringViaExchange("string")
                    else -> neighboringViaExchange("test")
                }
            }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("string")
    }

    "When in single expression in else case" {
        val condition = false
        val x = if (condition) "hello" else 123
        val result =
            aggregate(id0, pathRepresentation) {
                when (x) {
                    is String -> neighboringViaExchange("string")
                    else -> neighboringViaExchange("test")
                }
            }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    "When with nested function" {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = aggregate(id0, pathRepresentation) {
            fun test() {
                neighboringViaExchange("test")
            }

            fun test2() {
                neighboringViaExchange("test2")
            }
            when (x) {
                is String -> test2()
                else -> test()
            }
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test2")
    }
    "Nested when condition must be aligned" {
        val condition1 = false
        val condition2 = true
        val result = aggregate(0, pathRepresentation) {
            when {
                condition1 -> neighboringViaExchange("test")
                else -> when {
                    condition2 -> neighboringViaExchange("test2")
                    else -> neighboringViaExchange("test3")
                }
            }
        }
        val messageFor0 = result.toSend.messagesFor(id0)
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test2")
    }
})
