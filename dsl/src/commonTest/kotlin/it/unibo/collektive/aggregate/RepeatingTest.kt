package it.unibo.collektive.aggregate

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate
import it.unibo.collektive.neighbouring
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stack.Path
import it.unibo.collektive.utils.getPaths

class RepeatingTest : StringSpec({
    val id0 = IntId(0)
    val id1 = IntId(1)

    val double: (Int) -> Int = { it * 2 }
    val initV1: Int = 1

    "First time repeating" {
        aggregate(id0) {
            repeating(initV1, double) shouldBe 2
        }
    }

    "Repeating more than once" {
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

    "Repeating with lambda body should work fine" {
        val result = aggregate(id1) {
            repeating(initV1) {
                neighbouring(it * 2)
            }
        }
        result.result.local shouldBe initV1 * 2
        result.toSend.firstOrNull()?.getPaths()?.shouldContain(Path(listOf("repeating.1", "neighbouring.1", "exchange.1")))
    }
})
