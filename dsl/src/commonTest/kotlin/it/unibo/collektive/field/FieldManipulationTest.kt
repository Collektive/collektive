package it.unibo.collektive.field

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.field.operations.max
import it.unibo.collektive.field.operations.min
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class FieldManipulationTest : StringSpec({
    val double: (Int) -> Int = { it * 2 }

    // ids
    val id0 = 0
    val id1 = 1

    "Get the min value including self" {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val sharedField = neighboringViaExchange(double(3))
            sharedField.min(sharedField.localValue) shouldBe 6
        }

        aggregate(id1, network1) {
            val sharedField = neighboringViaExchange(double(2))
            sharedField.min(sharedField.localValue) shouldBe 4
        }
    }

    "Get min value non including self" {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val minValue = neighboringViaExchange(double(3)).min(Int.MAX_VALUE)
            minValue shouldBe Int.MAX_VALUE
        }

        aggregate(id1, network1) {
            val minValue = neighboringViaExchange(double(2)).min(Int.MAX_VALUE)
            minValue shouldBe 6
        }
    }

    "Get max value non including self" {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            val maxValue = neighboringViaExchange(double(3)).max(Int.MIN_VALUE)
            maxValue shouldBe Int.MIN_VALUE
        }

        aggregate(id1, network1) {
            val maxValue = neighboringViaExchange(double(2)).max(Int.MIN_VALUE)
            maxValue shouldBe 6
        }
    }
})
