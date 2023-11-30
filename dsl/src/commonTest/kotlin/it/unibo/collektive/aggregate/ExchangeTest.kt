package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field
import it.unibo.collektive.messages.OutboundMessage
import it.unibo.collektive.messages.SingleOutboundMessage
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stack.Path

class ExchangeTest : StringSpec({

    // device ids
    val id0 = IntId(0)
    val id1 = IntId(1)
    val id2 = IntId(2)
    val id3 = IntId(3)

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

    // expected
    val expected2 = 2
    val expected3 = 3
    val expected5 = 5
    val expected6 = 6
    val expected7 = 7
    val expected10 = 10

    val increaseOrDouble: (Field<Int>) -> Field<Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    "First time exchange should return the initial value" {
        aggregate(id0) {
            val res = exchange(initV1, increaseOrDouble)
            res.localValue shouldBe expected2
            messagesToSend() shouldBe OutboundMessage(
                id0,
                mapOf(path1 to SingleOutboundMessage(expected2, emptyMap())),
            )
        }
    }

    "Exchange should work between three aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, testNetwork1) {
            val res1 = exchange(initV1, increaseOrDouble)
            val res2 = exchange(initV2, increaseOrDouble)
            testNetwork1.read() shouldHaveSize 0
            res1.localValue shouldBe expected2
            res2.localValue shouldBe expected3
            messagesToSend() shouldBe OutboundMessage(
                id1,
                mapOf(
                    path1 to SingleOutboundMessage(expected2, emptyMap()),
                    path2 to SingleOutboundMessage(expected3, emptyMap()),
                ),
            )
        }

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, testNetwork2) {
            val res1 = exchange(initV3, increaseOrDouble)
            val res2 = exchange(initV4, increaseOrDouble)
            testNetwork2.read() shouldHaveSize 1

            val readMessages = testNetwork2.read().first().messages
            readMessages shouldHaveSize 2
            readMessages[path1] shouldBe expected2
            readMessages[path2] shouldBe expected3

            res1.localValue shouldBe expected6
            res2.localValue shouldBe expected5
            messagesToSend() shouldBe OutboundMessage(
                id2,
                mapOf(
                    path1 to SingleOutboundMessage(
                        expected6,
                        mapOf(id1 to expected3),
                    ),
                    path2 to SingleOutboundMessage(
                        expected5,
                        mapOf(id1 to expected6),
                    ),
                ),
            )
        }

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        aggregate(id3, testNetwork3) {
            val res1 = exchange(initV5, increaseOrDouble)
            val res2 = exchange(initV6, increaseOrDouble)
            val read = testNetwork3.read()
            read shouldHaveSize 2

            val sentFromDev1 = read.first { it.senderId == id1 }
            val sentFromDev2 = read.first { it.senderId == id2 }

            sentFromDev1.messages shouldHaveSize 2
            sentFromDev2.messages shouldHaveSize 2

            sentFromDev1.messages[path1] shouldBe expected2
            sentFromDev1.messages[path2] shouldBe expected3

            sentFromDev2.messages[path1] shouldBe expected6
            sentFromDev2.messages[path2] shouldBe expected5

            res1.localValue shouldBe expected10
            res2.localValue shouldBe expected7
            messagesToSend() shouldBe OutboundMessage(
                id3,
                mapOf(
                    path1 to SingleOutboundMessage(
                        expected10,
                        mapOf(
                            id1 to expected3,
                            id2 to expected7,
                        ),
                    ),
                    path2 to SingleOutboundMessage(
                        expected7,
                        mapOf(
                            id1 to expected6,
                            id2 to expected10,
                        ),
                    ),
                ),
            )
        }
    }
})
