package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.path.Path
import it.unibo.collektive.path.PathSummary
import it.unibo.collektive.path.impl.IdentityPathSummary

class RepeatingTest : StringSpec({
    val id0 = 0
    val id1 = 1

    val double: (Int) -> Int = { it * 2 }
    val initV1 = 1
    val pathRepresentation: (Path) -> PathSummary = { IdentityPathSummary(it) }

    "First time repeating" {
        aggregate(id0, pathRepresentation) {
            repeat(initV1, double) shouldBe 2
        }
    }

    "Repeating more than once" {
        var res = 0

        val testNetwork = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, pathRepresentation, testNetwork) {
            res = repeat(initV1, double)
            res = repeat(res, double)
        }
        res shouldBe 4
    }

    "Repeat with lambda body should work fine" {
        val testNetwork = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, pathRepresentation, testNetwork) {
            repeat(initV1) { it * 2 } shouldBe 2
        }
    }

    "Repeating should return the value passed in the yielding function" {
        val result = aggregate(id1, pathRepresentation) {
            repeating(initV1) {
                val nbr = neighboringViaExchange(it * 2).localValue
                nbr.yielding { "A string" }
            } shouldBe "A string"
        }
        result.toSend.messages.keys.size shouldBe 1
        result.toSend.messages.values.map { it.default } shouldContainAll listOf(2)
    }

    "Repeating should work fine even with null as value" {
        val testNetwork1 = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, pathRepresentation, testNetwork1) {
            repeating(initV1) {
                val mult = it * 2
                mult.yielding { "Hello".takeIf { mult < 1 } }
            } shouldBe null
        }
    }
})
