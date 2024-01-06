package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.networking.OutboundMessage
import it.unibo.collektive.networking.SingleOutboundMessage
import it.unibo.collektive.path.Path

class ExchangeTest : StringSpec({

    // initial values
    val initV1 = 1
    val initV2 = 2
    val initV3 = 3
    val initV4 = 4
    val initV5 = 5
    val initV6 = 6

    // paths
    val path1 = Path(listOf("exchange.1"))
    val path2 = Path(listOf("exchange.2"))
    val exchangingPath = Path("exchanging.1")

    // expected
    val expected2 = 2
    val expected3 = 3
    val expected5 = 5
    val expected6 = 6
    val expected7 = 7
    val expected10 = 10

    val increaseOrDouble: (Field<Int, Int>) -> Field<Int, Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    "First time exchange should return the initial value" {
        val result = aggregate(0) {
            val res = exchange(initV1, increaseOrDouble)
            res.localValue shouldBe expected2
        }
        result.toSend shouldBe OutboundMessage(
            0,
            mapOf(path1 to SingleOutboundMessage(expected2, emptyMap())),
        )
    }

    "Exchange should work between three aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, 1)
        val resultDevice1 = aggregate(1, testNetwork1) {
            val res1 = exchange(initV1, increaseOrDouble)
            val res2 = exchange(initV2, increaseOrDouble)
            testNetwork1.read() shouldHaveSize 0
            res1.localValue shouldBe expected2
            res2.localValue shouldBe expected3
        }

        resultDevice1.toSend shouldBe OutboundMessage(
            1,
            mapOf(
                path1 to SingleOutboundMessage(expected2, emptyMap()),
                path2 to SingleOutboundMessage(expected3, emptyMap()),
            ),
        )

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, 2)
        val resultDevice2 = aggregate(2, testNetwork2) {
            val res1 = exchange(initV3, increaseOrDouble)
            val res2 = exchange(initV4, increaseOrDouble)

            res1.localValue shouldBe expected6
            res2.localValue shouldBe expected5
        }

        resultDevice2.toSend shouldBe OutboundMessage(
            2,
            mapOf(
                path1 to SingleOutboundMessage(
                    expected6,
                    mapOf(1 to expected3),
                ),
                path2 to SingleOutboundMessage(
                    expected5,
                    mapOf(1 to expected6),
                ),
            ),
        )

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, 3)
        val resultDevice3 = aggregate(3, testNetwork3) {
            val res1 = exchange(initV5, increaseOrDouble)
            val res2 = exchange(initV6, increaseOrDouble)

            res1.localValue shouldBe expected10
            res2.localValue shouldBe expected7
        }

        resultDevice3.toSend shouldBe OutboundMessage(
            3,
            mapOf(
                path1 to SingleOutboundMessage(
                    expected10,
                    mapOf(
                        1 to expected3,
                        2 to expected7,
                    ),
                ),
                path2 to SingleOutboundMessage(
                    expected7,
                    mapOf(
                        1 to expected6,
                        2 to expected10,
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
})
