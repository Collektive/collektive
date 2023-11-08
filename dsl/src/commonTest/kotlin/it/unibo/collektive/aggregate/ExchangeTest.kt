package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.field.Field
import it.unibo.collektive.messages.AnisotropicMessage
import it.unibo.collektive.messages.IsotropicMessage
import it.unibo.collektive.messages.OutboundMessage
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

    val increaseOrDouble: (Field<Int>) -> Field<Int> = { f ->
        f.mapWithId { _, v -> if (v % 2 == 0) v + 1 else v * 2 }
    }

    "First time exchange should return the initial value" {
        aggregate(id0) {
            val res = exchange(initV1, increaseOrDouble)
            res shouldBe Field(id0, 2)
            messagesToSend() shouldBe setOf(IsotropicMessage(id0, mapOf(path1 to res.localValue)) as OutboundMessage)
        }
    }

    "Exchange should work between two aligned devices" {
        val nm = NetworkManager()
        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, condition, testNetwork1) {
            val res1 = exchange(initV1, increaseOrDouble)
            val res2 = exchange(initV2, increaseOrDouble)
            testNetwork1.read() shouldHaveSize 0
            res1 shouldBe Field(id1, 2)
            res2 shouldBe Field(id1, 3)
            messagesToSend() shouldBe setOf(
                IsotropicMessage(id1, mapOf(path1 to res1.localValue)),
                IsotropicMessage(id1, mapOf(path2 to res2.localValue)),
            )
        }

        i = 0
        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, condition, testNetwork2) {
            val res1 = exchange(initV3, increaseOrDouble)
            val res2 = exchange(initV4, increaseOrDouble)
            testNetwork2.read() shouldHaveSize 2
            res1 shouldBe Field(id2, 6, mapOf(id1 to 3))
            res2 shouldBe Field(id2, 5, mapOf(id1 to 6))
            messagesToSend() shouldBe setOf(
                IsotropicMessage(id2, mapOf(path1 to initV3 * 2)),
                IsotropicMessage(id2, mapOf(path2 to initV4 + 1)),
                AnisotropicMessage(id2, id1, mapOf(path1 to res1[id1], path2 to res2[id1])),
            )
        }

        i = 0
        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        aggregate(id3, condition, testNetwork3) {
            val res1 = exchange(initV5, increaseOrDouble)
            val res2 = exchange(initV6, increaseOrDouble)
            testNetwork3.read() shouldHaveSize 4
            res1 shouldBe Field(id3, 10, mapOf(id1 to 3, id2 to 7))
            res2 shouldBe Field(id3, 7, mapOf(id1 to 6, id2 to 10))
            messagesToSend() shouldBe setOf(
                IsotropicMessage(id3, mapOf(path1 to initV5 * 2)),
                IsotropicMessage(id3, mapOf(path2 to initV6 + 1)),
                AnisotropicMessage(id3, id1, mapOf(path1 to res1[id1], path2 to res2[id1])),
                AnisotropicMessage(id3, id2, mapOf(path1 to res1[id2], path2 to res2[id2])),
            )
        }
    }
})
