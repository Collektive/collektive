package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.networking.NetworkManager

class RepeatingTest : StringSpec({
    val id0 = IntId(0)
    val id1 = IntId(1)

    val double: (Int) -> Int = { it * 2 }
    val initV1: Int = 1

    "first time repeating" {
        aggregate(id0) {
            val res = repeating(initV1, double)
            res shouldBe 2
        }
    }

    "repeating more than once" {
        val nm = NetworkManager()
        var counter = 0
        val condition: () -> Boolean = { counter++ != 2 }
        var res = 0

        val networkImpl = NetworkImplTest(nm, id1)
        aggregate(id1, condition, networkImpl) {
            res = repeating(initV1, double)
        }
        res shouldBe 4
    }

//    TODO("Add test with nested calls and check on paths")
})
