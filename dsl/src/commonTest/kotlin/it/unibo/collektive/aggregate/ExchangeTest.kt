package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage
import it.unibo.collektive.path.Path

class ExchangeTest : StringSpec({

    // device ids
    val id0 = IntId(0)
    val id1 = IntId(1)
    val id2 = IntId(2)
    val id3 = IntId(3)

    // paths
    val path1 = Path(listOf("exchange.1"))
    val path2 = Path(listOf("exchange.2"))
    val exchangingPath = Path("exchanging.1")

    val increaseOrDouble: (Field<Int>) -> Field<Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    "First time exchange should return the initial value" {
        val result = aggregate(id0) {
            val res = exchange(1, increaseOrDouble)
            res.localValue shouldBe 2
        }
        result.toSend shouldBe OutboundMessage(
            id0,
            mapOf(path1 to SingleOutboundMessage(2, emptyMap())),
        )
    }

    "Exchange should work between three aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        val resultDevice1 = aggregate(id1, testNetwork1) {
            val res1 = exchange(1, increaseOrDouble)
            val res2 = exchange(2, increaseOrDouble)
            testNetwork1.read() shouldHaveSize 0
            res1.localValue shouldBe 2
            res2.localValue shouldBe 3
        }

        resultDevice1.toSend shouldBe OutboundMessage(
            id1,
            mapOf(
                path1 to SingleOutboundMessage(2, emptyMap()),
                path2 to SingleOutboundMessage(3, emptyMap()),
            ),
        )

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        val resultDevice2 = aggregate(id2, testNetwork2) {
            val res1 = exchange(3, increaseOrDouble)
            val res2 = exchange(4, increaseOrDouble)

            res1.localValue shouldBe 6
            res2.localValue shouldBe 5
        }

        resultDevice2.toSend shouldBe OutboundMessage(
            id2,
            mapOf(
                path1 to SingleOutboundMessage(
                    6,
                    mapOf(id1 to 3),
                ),
                path2 to SingleOutboundMessage(
                    5,
                    mapOf(id1 to 6),
                ),
            ),
        )

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        val resultDevice3 = aggregate(id3, testNetwork3) {
            val res1 = exchange(5, increaseOrDouble)
            val res2 = exchange(6, increaseOrDouble)

            res1.localValue shouldBe 10
            res2.localValue shouldBe 7
        }

        resultDevice3.toSend shouldBe OutboundMessage(
            id3,
            mapOf(
                path1 to SingleOutboundMessage(
                    10,
                    mapOf(
                        id1 to 3,
                        id2 to 7,
                    ),
                ),
                path2 to SingleOutboundMessage(
                    7,
                    mapOf(
                        id1 to 6,
                        id2 to 10,
                    ),
                ),
            ),
        )
    }

    "Exchange can yield a result but return a different value" {
        val result = aggregate(id0) {
            val xcRes = exchanging(1) {
                val fieldResult = it + 1
                fieldResult.yielding { fieldResult.map { value -> "return: $value" } }
            }
            xcRes.toMap() shouldBe mapOf(id0 to "return: 2")
        }
        result.toSend shouldBe OutboundMessage(
            id0,
            mapOf(exchangingPath to SingleOutboundMessage(2)),
        )
    }

    "Exchange can yield a result of nullable values" {
        val result = aggregate(id0) {
            val xcRes = exchanging(1) {
                val fieldResult = it + 1
                fieldResult.yielding { fieldResult.map { value -> value.takeIf { value > 10 } } }
            }
            xcRes.toMap() shouldBe mapOf(id0 to null)
        }
        result.toSend shouldBe OutboundMessage(
            id0,
            mapOf(exchangingPath to SingleOutboundMessage(2)),
        )
    }
})
