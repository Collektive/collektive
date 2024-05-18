package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class ExchangeTest : StringSpec({
    val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    "First time exchange should return the initial value" {
        val result = aggregate(0) {
            val res = exchange(1, increaseOrDouble)
            res.localValue shouldBe 2
        }
        val messages = result.toSend.messagesFor(1)
        messages.keys shouldHaveSize 1
        messages.values.toList() shouldBe listOf(2)
    }

    "Exchange should work between three aligned devices" {
        val networkManager = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(networkManager, 1)
        val resultDevice1 = aggregate(1, testNetwork1) {
            val res1 = exchange(1, increaseOrDouble)
            val res2 = exchange(2, increaseOrDouble)
            testNetwork1.read() shouldHaveSize 0
            res1.localValue shouldBe 2
            res2.localValue shouldBe 3
        }
        val messagesFor2 = resultDevice1.toSend.messagesFor(2)
        messagesFor2 shouldHaveSize 2
        messagesFor2.values.toList() shouldBe listOf(2, 3)

        // Device 2
        val testNetwork2 = NetworkImplTest(networkManager, 2)
        val resultDevice2 = aggregate(2, testNetwork2) {
            val res1 = exchange(3, increaseOrDouble)
            val res2 = exchange(4, increaseOrDouble)
            res1.localValue shouldBe 6
            res2.localValue shouldBe 5
        }
        val messagesFor1 = resultDevice2.toSend.messagesFor(1)
        val messagesForAnyoneElse = resultDevice2.toSend.messagesFor(Int.MIN_VALUE)
        messagesFor1 shouldHaveSize 2
        messagesForAnyoneElse shouldHaveSize 2
        messagesFor1.values.toList() shouldBe listOf(3, 6)
        messagesForAnyoneElse.values.toList() shouldBe listOf(6, 5)

        // Device 3
        val testNetwork3 = NetworkImplTest(networkManager, 3)
        val resultDevice3 = aggregate(3, testNetwork3) {
            val res1 = exchange(5, increaseOrDouble)
            val res2 = exchange(6, increaseOrDouble)
            res1.localValue shouldBe 10
            res2.localValue shouldBe 7
        }
        val messagesFrom3To1 = resultDevice3.toSend.messagesFor(1)
        val messagesFrom3To2 = resultDevice3.toSend.messagesFor(2)
        val messagesFrom3ToAnyoneElse = resultDevice3.toSend.messagesFor(Int.MIN_VALUE)
        messagesFrom3To1 shouldHaveSize 2
        messagesFrom3To2 shouldHaveSize 2
        messagesFrom3ToAnyoneElse shouldHaveSize 2
        messagesFrom3ToAnyoneElse.values.toList() shouldBe listOf(10, 7)
        messagesFrom3To1.values.toList() shouldBe listOf(3, 6)
        messagesFrom3To2.values.toList() shouldBe listOf(7, 10)
    }

    "Exchange can yield a result but return a different value" {
        val result = aggregate(0) {
            val xcRes = exchanging(1) {
                val fieldResult = it + 1
                fieldResult.yielding { fieldResult.map { value -> "return: $value" } }
            }
            xcRes.toMap() shouldBe mapOf(0 to "return: 2")
        }
        val messages = result.toSend.messagesFor(1)
        messages shouldHaveSize 1
        messages.values.toList() shouldBe listOf(2)
    }

    "Exchange can yield a result of nullable values" {
        val result = aggregate(0) {
            val xcRes = exchanging(1) {
                val fieldResult = it + 1
                fieldResult.yielding { fieldResult.map { value -> value.takeIf { value > 10 } } }
            }
            xcRes.toMap() shouldBe mapOf(0 to null)
        }
        val messages = result.toSend.messagesFor(1)
        messages shouldHaveSize 1
        messages.values.toList() shouldBe listOf(2)
    }

    "Exchange should produce a message with no overrides when producing a constant field" {
        val programUnderTest: Aggregate<Int>.() -> Unit = {
            exchanging(0) {
                it.mapToConstantField(10).yielding { it.mapToConstantField("singleton") }
            }
        }
        val networkManager = NetworkManager()

        // Three devices linked together executed for 2 round each.
        (0..5).forEach { iteration ->
            val id = iteration % 3
            val res = aggregate(id, emptyMap(), networkManager.receive(id), programUnderTest)
                .also { networkManager.send(it.toSend) }
            val toUnknown = res.toSend.messagesFor(Int.MIN_VALUE)
            toUnknown shouldHaveSize 1
            val next = res.toSend.messagesFor((id + 1) % 3)
            val previous = res.toSend.messagesFor((id + 2) % 3)
            // When a constant field is used, the map of overrides should be empty
            next shouldBe toUnknown
            previous shouldBe toUnknown
        }
    }
})
