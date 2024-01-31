package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class NestedCallsTest : StringSpec({

    fun Aggregate<Int>.foo(id: Int) = neighboringViaExchange(id).map { v -> v + 1.0 }

    fun Aggregate<Int>.gradient(): Double =
        share(Double.POSITIVE_INFINITY) { dist ->
            when (localId) {
                0 -> 0.0
                else -> (foo(localId) + dist).excludeSelf().minOfOrNull { it.value } ?: Double.POSITIVE_INFINITY
            }
        }

    "Nested alignment should work fine" {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, 0)
        val network1 = NetworkImplTest(networkManager, 1)

        val collektiveDevice0 = Collektive(0, network0) { gradient() }
        collektiveDevice0.cycle() shouldBe 0

        val collektiveDevice1 = Collektive(1, network1) { gradient() }
        collektiveDevice1.cycle() shouldBe 2
    }
})
