package it.unibo.collektive.alignment

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.aggregate.ops.share
import it.unibo.collektive.stack.Path

class TestAlignment : StringSpec({
    "The alignment should be performed consistently also for the same aggregate operation called multiple times " +
        "(issue #51)" {
            val result = aggregate(IntId(0)) {
                neighbouring(10) // path -> [neighbouring.1] = 10
                share(0) {
                    requireNotNull(neighbouring(20).localValue) // path -> [share.1, neighbouring.2] = 20
                } // path -> [sharing.1] = Field(...)
                neighbouring(30) // path -> [neighbouring.3] = 30
                5
            }

            result.result shouldBe 5
            result.toSend.messages.keys shouldHaveSize 4 // 4 paths of alignment
            result.toSend.messages.keys shouldContainAll setOf(
                Path(listOf("invoke.1", "neighbouring.1", "exchange.1")),
                Path(listOf("invoke.1", "share.1", "sharing.1", "exchange.1", "neighbouring.2", "exchange.1")),
                Path(listOf("invoke.1", "share.1", "sharing.1", "exchange.1")),
                Path(listOf("invoke.1", "neighbouring.3", "exchange.1")),
            )
        }
})
