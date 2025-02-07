package it.unibo.collektive.aggregate

import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.Aggregate.Companion.exchange
import it.unibo.collektive.aggregate.api.Aggregate.Companion.exchanging
import it.unibo.collektive.field.Field
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import kotlin.test.Test

class ExchangeTest {
    val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    @Test
    fun `First time exchange should return the initial value`() {
        val result =
            aggregate(0) {
                val res = exchange(1, increaseOrDouble)
                res.localValue shouldBe 2
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        messages.keys shouldHaveSize 1
        messages.values.toList() shouldBe listOf(2)
    }

    @Test
    fun `Exchange should work between three aligned devices`() {
        val networkManager = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(networkManager, 1)
        val resultDevice1 =
            aggregate(1, testNetwork1) {
                val res1 = exchange(1, increaseOrDouble)
                val res2 = exchange(2, increaseOrDouble)
                testNetwork1.currentInbound().neighbors shouldHaveSize 0
                res1.localValue shouldBe 2
                res2.localValue shouldBe 3
            }
        val messagesFor2 = resultDevice1.toSend.prepareMessageFor(2).sharedData
        messagesFor2 shouldHaveSize 2
        messagesFor2.values.toList() shouldBe listOf(2, 3)

        // Device 2
        val testNetwork2 = NetworkImplTest(networkManager, 2)
        val resultDevice2 =
            aggregate(2, testNetwork2) {
                val res1 = exchange(3, increaseOrDouble)
                val res2 = exchange(4, increaseOrDouble)
                res1.localValue shouldBe 6
                res2.localValue shouldBe 5
            }
        val messagesFor1 = resultDevice2.toSend.prepareMessageFor(1).sharedData
        val messagesForAnyoneElse = resultDevice2.toSend.prepareMessageFor(Int.MIN_VALUE).sharedData
        messagesFor1 shouldHaveSize 2
        messagesForAnyoneElse shouldHaveSize 2
        messagesFor1.values.toList() shouldBe listOf(3, 6)
        messagesForAnyoneElse.values.toList() shouldBe listOf(6, 5)

        // Device 3
        val testNetwork3 = NetworkImplTest(networkManager, 3)
        val resultDevice3 =
            aggregate(3, testNetwork3) {
                val res1 = exchange(5, increaseOrDouble)
                val res2 = exchange(6, increaseOrDouble)
                res1.localValue shouldBe 10
                res2.localValue shouldBe 7
            }
        val messagesFrom3To1 = resultDevice3.toSend.prepareMessageFor(1).sharedData
        val messagesFrom3To2 = resultDevice3.toSend.prepareMessageFor(2).sharedData
        val messagesFrom3ToAnyoneElse = resultDevice3.toSend.prepareMessageFor(Int.MIN_VALUE).sharedData
        messagesFrom3To1 shouldHaveSize 2
        messagesFrom3To2 shouldHaveSize 2
        messagesFrom3ToAnyoneElse shouldHaveSize 2
        messagesFrom3ToAnyoneElse.values.toList() shouldBe listOf(10, 7)
        messagesFrom3To1.values.toList() shouldBe listOf(3, 6)
        messagesFrom3To2.values.toList() shouldBe listOf(7, 10)
    }

    @Test
    fun `Exchange can yield a result but return a different value`() {
        val result =
            aggregate(0) {
                val xcRes =
                    exchanging(1) {
                        val fieldResult = it + 1
                        fieldResult.yielding { fieldResult.map { value -> "return: $value" } }
                    }
                xcRes.toMap() shouldBe mapOf(0 to "return: 2")
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        messages shouldHaveSize 1
        messages.values.toList() shouldBe listOf(2)
    }

    @Test
    fun `Exchange can yield a result of nullable values`() {
        val result =
            aggregate(0) {
                val xcRes =
                    exchanging(1) {
                        val fieldResult = it + 1
                        fieldResult.yielding { fieldResult.map { value -> value.takeIf { value > 10 } } }
                    }
                xcRes.toMap() shouldBe mapOf(0 to null)
            }
        val messages = result.toSend.prepareMessageFor(1).sharedData
        messages shouldHaveSize 1
        messages.values.toList() shouldBe listOf(2)
    }

    @Test
    fun `Exchange should produce a message with no overrides when producing a constant field`() {
        val programUnderTest: Aggregate<Int>.() -> Unit = {
            exchanging(0) {
                it.mapToConstantField(10).yielding { it.mapToConstantField("singleton") }
            }
        }
        val networkManager = NetworkManager()

        // Three devices linked together executed for 2 round each.
        (0..5).forEach { iteration ->
            val id = iteration % 3
            val res =
                aggregate(id, emptyMap(), networkManager.receiveMessageFor(id), compute = programUnderTest)
                    .also { networkManager.send(id, it.toSend) }
            val toUnknown = res.toSend.prepareMessageFor(Int.MIN_VALUE).sharedData
            toUnknown shouldHaveSize 1
            val next = res.toSend.prepareMessageFor((id + 1) % 3).sharedData
            val previous = res.toSend.prepareMessageFor((id + 2) % 3).sharedData
            // When a constant field is used, the map of overrides should be empty
            next shouldBe toUnknown
            previous shouldBe toUnknown
        }
    }
}
