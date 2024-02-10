package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.min
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class NestedCallsTest : StringSpec({

    fun Aggregate<Int>.foo(id: Int) = neighboringViaExchange(id.toDouble())

    fun Aggregate<Int>.bar(): Double {
        return share(Double.POSITIVE_INFINITY) { dist ->
            when (localId) {
                0 -> 0.0
                else -> (foo(localId) + dist).min(Double.POSITIVE_INFINITY)
            }
        }
    }

    "This is a regression test for the issue #207" {
        val networkManager = NetworkManager()
        val network0 = NetworkImplTest(networkManager, 0)
        val network1 = NetworkImplTest(networkManager, 1)

        val collektiveDevice0 = Collektive(0, network0) { bar() }
        collektiveDevice0.cycle() shouldBe 0

        val collektiveDevice1 = Collektive(1, network1) { bar() }
        println(network1.read())
        collektiveDevice1.cycle() shouldBe Double.POSITIVE_INFINITY
    }
})
