package it.unibo.collektive.alignment

import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.api.operators.neighboring
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.path.Path

class TestAlignment : StringSpec({
    "The alignment should be performed also for the same aggregate operation called multiple times (issue #51)" {
        val result =
            aggregate(IntId(0)) {
                neighboring(10) // path -> [neighboring.1] = 10
                share(0) {
                    requireNotNull(neighboring(20).localValue) // path -> [share.1, neighboring.2] = 20
                } // path -> [sharing.1] = Field(...)
                neighboring(30) // path -> [neighboring.3] = 30
                5
            }

        result.result shouldBe 5
        result.toSend.messages.keys shouldHaveSize 4 // 4 paths of alignment
        result.toSend.messages.keys shouldContainAll
            setOf(
                Path("neighboring.1", "exchange.1"),
                Path("share.1", "sharing.1", "exchange.1", "neighboring.2", "exchange.1"),
                Path("share.1", "sharing.1", "exchange.1"),
                Path("neighboring.3", "exchange.1"),
            )
    }
    "Alignment must fail clearly when entries try to override each other" {
        val exception = shouldThrowUnit<IllegalStateException> {
            aggregate(IntId(0)) {
                kotlin.repeat(2) {
                    neighboring(0)
                }
            }
        }
        exception.message shouldContain "Aggregate alignment clash by multiple aligned calls with the same path"
    }
})
