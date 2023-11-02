package it.unibo.collektive.alignment

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.aggregate
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.aggregate.ops.share
import it.unibo.collektive.stack.Path
import it.unibo.collektive.utils.getPaths

class TestAlignment : StringSpec({
    "The alignment should be performed consistently also for the same aggregate operation called multiple times " +
        "(issue #51)" {
            val result = aggregate(IntId(0)) {
                neighbouring(10) // path -> [neighbouring.1] = 10
                share(0) {
                    neighbouring(20) // path -> [sharing.1, neighbouring.2] = 20
                } // path -> [sharing.1] = Field(...)
                neighbouring(30) // path -> [neighbouring.3] = 30
                5
            }

            result.result shouldBe 5
            var paths = emptySet<Path>()
            result.toSend.forEach { paths = paths + it.getPaths() }
            paths.size shouldBe 4 // 4 paths of alignment
            paths shouldContainAll setOf(
                Path(listOf("neighbouring.1", "exchange.1")),
                Path(listOf("share.1", "exchange.1", "neighbouring.2", "exchange.1")),
                Path(listOf("share.1", "exchange.1")),
                Path(listOf("neighbouring.3", "exchange.1")),
            )
        }
})
