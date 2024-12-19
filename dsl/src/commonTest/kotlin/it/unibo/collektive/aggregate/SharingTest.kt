package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.aggregate.api.operators.sharing
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.operations.max
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class SharingTest : StringSpec({
    val findMax: (Field<*, Int>) -> Int = { e -> e.max(e.localValue) }

    "first time sharing" {
        aggregate(0) {
            val res = share(1, findMax)
            res shouldBe 1
        }
    }

    "Share with two aligned devices" {
        val nm = NetworkManager()

        // Device 1
        val testNetwork1 = NetworkImplTest(nm, 1)
        aggregate(1, testNetwork1) {
            val r1 = share(1, findMax)
            val r2 = share(2, findMax)
            val r3 = share(10, findMax)
            r1 shouldBe 1
            r2 shouldBe 2
            r3 shouldBe 10
        }

        // Device 2
        val testNetwork2 = NetworkImplTest(nm, 2)
        aggregate(2, testNetwork2) {
            val r1 = share(2, findMax)
            val r2 = share(7, findMax)
            val r3 = share(4, findMax)
            r1 shouldBe 2
            r2 shouldBe 7
            r3 shouldBe 10
        }

        // Device 3
        val testNetwork3 = NetworkImplTest(nm, 3)
        aggregate(3, testNetwork3) {
            val r1 = share(5, findMax)
            val r2 = share(1, findMax)
            val r3 = share(3, findMax)
            r1 shouldBe 5
            r2 shouldBe 7
            r3 shouldBe 10
        }
    }

    "Share with lambda body should work fine" {
        val testNetwork = NetworkImplTest(NetworkManager(), 1)

        aggregate(1, testNetwork) {
            val res =
                share(1) {
                    it.max(it.localValue)
                }
            res shouldBe 1
        }
    }

    "Sharing should return the value passed in the yielding function" {
        val testNetwork = NetworkImplTest(NetworkManager(), 1)

        aggregate(1, testNetwork) {
            val res =
                sharing(1) {
                    val min = it.max(it.localValue)
                    min.yielding { "A string" }
                }
            res shouldBe "A string"
        }
    }

    "Sharing should work fine even with null as value" {
        val testNetwork = NetworkImplTest(NetworkManager(), 1)

        aggregate(1, testNetwork) {
            val res =
                sharing(1) {
                    val min = it.min(it.localValue)
                    min.yielding { "Hello".takeIf { min > 1 } }
                }
            res shouldBe null
        }
    }
})
