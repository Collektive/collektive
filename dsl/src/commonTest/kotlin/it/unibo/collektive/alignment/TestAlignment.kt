package it.unibo.collektive.alignment

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.aggregate
import it.unibo.collektive.stack.Path

class TestAlignment : FreeSpec({
    "The alignment" - {
        "should be performed consistently also for the same aggregate operation called multiple times (issue #51)" {
            val result = aggregate {
                neighbouring(10) // path -> [neighbouring.1] = 10
                sharing(0) {
                    neighbouring(20) // path -> [sharing.1, neighbouring.2] = 20
                } // path -> [sharing.1] = Field(...)
                neighbouring(30) // path -> [neighbouring.3] = 30
                5
            }

            result.result shouldBe 5
            result.toSend.keys.size shouldBe 4 // 4 paths of alignment
            result.toSend.keys shouldBe setOf(
                Path(listOf("neighbouring.1")),
                Path(listOf("sharing.1", "neighbouring.2")),
                Path(listOf("sharing.1")),
                Path(listOf("neighbouring.3")),
            )
        }
    }
})
