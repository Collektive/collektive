package it.unibo.collektive.alignment

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.stack.Path

class BranchAlignment : StringSpec({
    val id0 = IntId(0)

    "The branch alignment should occur also for nested functions" {
        val result = aggregate(id0) {
            val condition = true
            fun test() {
                neighbouring("test")
            }

            fun test2() {
                test()
            }
            if (condition) {
                test2()
            }
        }
        result.toSend.messages.keys shouldHaveSize 1 // 1 path of alignment
        result.toSend.messages.keys shouldContain Path(
            listOf("branch[condition, true]", "test2.1", "test.1", "neighbouring.1", "exchange.1"),
        )
    }
    "The branch alignment should not occur in non aggregate context" {
        val result = aggregate(id0) {
            val condition = true
            fun test(): String = "hello"
            fun test2() {
                test()
            }
            if (condition) {
                test2()
            }
        }
        result.toSend.messages.keys shouldHaveSize 0 // 0 path of alignment
    }
})
