package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage
import it.unibo.collektive.path.Path

class ExchangeTest : StringSpec({

    // paths
    val path1 = Path(listOf("exchange.1"))
    val path2 = Path(listOf("exchange.2"))
    val exchangingPath = Path("exchanging.1")

    val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    "First time exchange should return the initial value" {
        val result = aggregate(0) {
            val res = exchange(1, increaseOrDouble)
            res.localValue shouldBe 2
        }
        result.toSend shouldBe OutboundMessage(
            0,
            mapOf(path1 to SingleOutboundMessage(2, emptyMap())),
        )
    }

    "Exchange should work between three aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, 1)
        val resultDevice1 = aggregate(1, testNetwork1) {
            val res1 = exchange(1, increaseOrDouble)
            val res2 = exchange(2, increaseOrDouble)
            testNetwork1.read() shouldHaveSize 0
            res1.localValue shouldBe 2
            res2.localValue shouldBe 3
        }

        resultDevice1.toSend shouldBe OutboundMessage(
            1,
            mapOf(
                path1 to SingleOutboundMessage(2, emptyMap()),
                path2 to SingleOutboundMessage(3, emptyMap()),
            ),
        )

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, 2)
        val resultDevice2 = aggregate(2, testNetwork2) {
            val res1 = exchange(3, increaseOrDouble)
            val res2 = exchange(4, increaseOrDouble)

            res1.localValue shouldBe 6
            res2.localValue shouldBe 5
        }

        resultDevice2.toSend shouldBe OutboundMessage(
            2,
            mapOf(
                path1 to SingleOutboundMessage(
                    6,
                    mapOf(1 to 3),
                ),
                path2 to SingleOutboundMessage(
                    5,
                    mapOf(1 to 6),
                ),
            ),
        )

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, 3)
        val resultDevice3 = aggregate(3, testNetwork3) {
            val res1 = exchange(5, increaseOrDouble)
            val res2 = exchange(6, increaseOrDouble)

            res1.localValue shouldBe 10
            res2.localValue shouldBe 7
        }

        resultDevice3.toSend shouldBe OutboundMessage(
            3,
            mapOf(
                path1 to SingleOutboundMessage(
                    10,
                    mapOf(
                        1 to 3,
                        2 to 7,
                    ),
                ),
                path2 to SingleOutboundMessage(
                    7,
                    mapOf(
                        1 to 6,
                        2 to 10,
                    ),
                ),
            ),
        )
    }

    "Exchange can yield a result but return a different value" {
        val result = aggregate(0) {
            val xcRes = exchanging(1) {
                val fieldResult = it + 1
                fieldResult.yielding { fieldResult.map { value -> "return: $value" } }
            }
            xcRes.toMap() shouldBe mapOf(0 to "return: 2")
        }
        result.toSend shouldBe OutboundMessage(
            0,
            mapOf(exchangingPath to SingleOutboundMessage(2)),
        )
    }

    "Exchange can yield a result of nullable values" {
        val result = aggregate(0) {
            val xcRes = exchanging(1) {
                val fieldResult = it + 1
                fieldResult.yielding { fieldResult.map { value -> value.takeIf { value > 10 } } }
            }
            xcRes.toMap() shouldBe mapOf(0 to null)
        }
        result.toSend shouldBe OutboundMessage(
            0,
            mapOf(exchangingPath to SingleOutboundMessage(2)),
        )
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
            val res = aggregate(id, networkManager.receive(id), emptyMap(), programUnderTest)
                .also { networkManager.send(it.toSend) }
            res.toSend.messages.values.size shouldBe 1
            // When the SingletonField is used, the map of overrides should be empty
            res.toSend.messages.values.firstOrNull()?.let { it.overrides shouldBe mapOf() }
        }
    }
})
