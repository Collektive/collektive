package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.SharingContext
import it.unibo.collektive.aggregate
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.max
import it.unibo.collektive.field.min
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.networking.NetworkManager
import it.unibo.collektive.share

class SharingTest : StringSpec({
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
    val initV7 = 7
    val initV10 = 10

    val findMax: SharingContext<Int, Int>.(Field<Int>) -> Int = { e -> e.toMap().maxBy { it.value }.value }

    "first time sharing" {
        aggregate(id0) {
            val res = share(initV1, findMax)
            res shouldBe 1
        }
    }

    "Share with two aligned devices" {
        val nm = NetworkManager()
        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, condition, testNetwork1) {
            val r1 = share(initV1, findMax)
            val r2 = share(initV2, findMax)
            val r3 = share(initV10, findMax)
            r1 shouldBe initV1
            r2 shouldBe initV2
            r3 shouldBe initV10
        }

        i = 0
        // Device 2
        val testNetwork2 = NetworkImplTest(nm, id2)
        aggregate(id2, condition, testNetwork2) {
            val r1 = share(initV2, findMax)
            val r2 = share(initV7, findMax)
            val r3 = share(initV4, findMax)
            r1 shouldBe initV2
            r2 shouldBe initV7
            r3 shouldBe initV10
        }

        i = 0
        // Device 3
        val testNetwork3 = NetworkImplTest(nm, id3)
        aggregate(id3, condition, testNetwork3) {
            val r1 = share(initV5, findMax)
            val r2 = share(initV1, findMax)
            val r3 = share(initV3, findMax)
            r1 shouldBe initV5
            r2 shouldBe initV7
            r3 shouldBe initV10
        }
    }

    "Sharing should work fine even with null as value" {
        val nm = NetworkManager()
        var i = 0
        val condition: () -> Boolean = { i++ < 1 }

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, id1)
        aggregate(id1, condition, testNetwork1) {
            val res1 = share(initV1) {
                it.max()?.value!!
            }
            res1 shouldBe initV1

            val res2 = share(initV1) {
                it.max()?.value!! butReturn "A string"
            }
            res2 shouldBe "A string"

            val res3 = share(initV1) {
                val min = it.min()?.value!!
                min butReturn if (min > 1) "Hello" else null
            }
            res3 shouldBe null
        }
    }
})
