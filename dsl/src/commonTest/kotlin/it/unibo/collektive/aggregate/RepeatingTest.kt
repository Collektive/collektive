package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stack.Path

class RepeatingTest : StringSpec({
    val id0 = IntId(0)
    val id1 = IntId(1)

    val double: (Int) -> Int = { it * 2 }
    val initV1: Int = 1

    "First time repeating" {
        aggregate(id0) {
            repeat(initV1, double) shouldBe 2
        }
    }

    "Repeating more than once" {
        var res = 0

        val testNetwork = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, testNetwork) {
            res = repeat(initV1, double)
            res = repeat(res, double)
        }
        res shouldBe 4
    }

    "Repeat with lambda body should work fine" {
        val testNetwork = NetworkImplTest(NetworkManager(), id1)

        aggregate(id1, testNetwork) {
            val res = repeat(initV1) {
                it * 2
            }
            res shouldBe 2
        }
    }

    "Repeating should return the value passed in the yielding function" {
        val result = aggregate(id1) {
            val res = repeating(initV1) {
                val nbr = neighbouring(it * 2).localValue
                nbr.yielding { "A string" }
            }
            res shouldBe "A string"
        }
        result.toSend.messages.keys shouldContain Path(
            listOf("invoke.1", "repeating.1", "neighbouring.1", "exchange.1"),
        )
    }

    "Repeating should work fine even with null as value" {
        val testNetwork1 = NetworkImplTest(NetworkManager(), id1)

        aggregate(id1, testNetwork1) {
            val res = repeating(initV1) {
                val mult = it * 2
                mult.yielding { "Hello".takeIf { mult < 1 } }
            }
            res shouldBe null
        }
    }
})
