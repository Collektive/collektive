package it.unibo.collektive.field

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import it.unibo.collektive.Collektive.Companion.aggregate
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
            neighboring(double(3))
        }

        aggregate(id1, network1) {
            val res = neighboring(double(2)).minWithSelf()
            res shouldNotBe null
            res shouldBe 4
        }
    }

    "Get min value non including self" {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            neighboring(double(3))
        }

        aggregate(id1, network1) {
            val res = neighboring(double(2)).min(Int.MAX_VALUE)
            res shouldNotBe null
            res shouldBe 6
        }
    }

    "Get max value non including self" {
        val nm = NetworkManager()
        val network0 = NetworkImplTest(nm, id0)
        val network1 = NetworkImplTest(nm, id1)

        aggregate(id0, network0) {
            neighboring(double(3))
        }

        aggregate(id1, network1) {
            val res = neighboring(double(2)).max(Int.MIN_VALUE)
            res shouldNotBe null
            res shouldBe 6
        }
    }
})
