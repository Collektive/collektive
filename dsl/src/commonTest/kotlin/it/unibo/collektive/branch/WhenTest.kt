package it.unibo.collektive.branch

import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import kotlin.test.Test

class WhenTest {
    val id0 = 0

    private fun programUnderTest(input: Any) =
        aggregate(id0) {
            when (input) {
                is String -> neighboringViaExchange("string")
                else -> neighboringViaExchange("test")
            }
        }

    @Test
    fun `When in single expression`() {
        val condition = true
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x)
        val messageFor0 = result.toSend.prepareMessageFor(id0).sharedData
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("string")
    }

    @Test
    fun `When in single expression in else case`() {
        val condition = false
        val x = if (condition) "hello" else 123
        val result = programUnderTest(x)
        val messageFor0 = result.toSend.prepareMessageFor(id0).sharedData
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test")
    }

    @Test
    fun `When with nested function`() {
        val condition = true
        val x = if (condition) "hello" else 123
        val result =
            aggregate(id0) {
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
        val messageFor0 = result.toSend.prepareMessageFor(id0).sharedData
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test2")
    }

    @Test
    fun `Nested when condition must be aligned`() {
        val condition1 = false
        val condition2 = true
        val result =
            aggregate(0) {
                when {
                    condition1 -> neighboringViaExchange("test")
                    else ->
                        when {
                            condition2 -> neighboringViaExchange("test2")
                            else -> neighboringViaExchange("test3")
                        }
                }
            }
        val messageFor0 = result.toSend.prepareMessageFor(id0).sharedData
        messageFor0 shouldHaveSize 1
        messageFor0.values.toList() shouldBe listOf("test2")
    }
}
