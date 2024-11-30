package it.unibo.collektive.aggregate

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import it.unibo.collektive.testing.Round.roundFor

class EvolvingTest : StringSpec({
    val id0 = 0
    val id1 = 1

    val double: (Int) -> Int = { it * 2 }
    val initV1 = 1

    "First time evolving" {
        aggregate(id0) {
            evolve(initV1, double) shouldBe 2
        }
    }

    "Evolving more than once" {
        var res = 0

        val testNetwork = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, testNetwork) {
            res = evolve(initV1, double)
            res = evolve(res, double)
        }
        res shouldBe 4
    }

    "Evolve with lambda body should work fine" {
        val testNetwork = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, testNetwork) {
            evolve(initV1) { it * 2 } shouldBe 2
        }
    }

    "Evolving should return the value passed in the yielding function" {
        val steps = 10
        val producedResult =
            roundFor(steps, deviceId = 0) {
                val evolvingRes =
                    evolving(0) {
                        (it + 1).yielding { "A string" }
                    }
                evolvingRes shouldBe "A string"
                evolvingRes
            }
        producedResult.newState.values.size shouldBe 1
        producedResult.newState.values shouldContain steps
    }

    "Evolving should work fine even with null as value" {
        val testNetwork1 = NetworkImplTest(NetworkManager(), id1)
        aggregate(id1, testNetwork1) {
            evolving(initV1) {
                val mult = it * 2
                mult.yielding { "Hello".takeIf { mult < 1 } }
            } shouldBe null
        }
    }

    "When the evolving returns a Field an exception must be raised" {
        shouldThrow<IllegalStateException> {
            aggregate(id1) {
                exchange(0) { field ->
                    evolve(field) { it + 1 }
                }
            }
        }
    }
})
