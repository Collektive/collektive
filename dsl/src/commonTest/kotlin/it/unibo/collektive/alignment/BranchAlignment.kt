package it.unibo.collektive.alignment

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe
import it.unibo.collektive.stack.Path

class BranchAlignment : FreeSpec({
    "The branch alignment" - {
        "should occur also for nested functions" {
            val result = it.unibo.collektive.aggregate {
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

            result.toSend.keys.size shouldBe 1 // 1 path of alignment
            result.toSend.keys shouldContainAll setOf(
                Path(listOf("alignedOn.1", "(condition, true)", "test2.1", "test.1", "neighbouring.1")),
            )
        }
        "should not occur in non aggregate context" {
            val result = it.unibo.collektive.aggregate {
                val condition = true
                fun test(): String = "hello"

                fun test2() {
                    test()
                }

                if (condition) {
                    test2()
                }
            }
            result.toSend.keys.size shouldBe 0 // 0 paths of alignment
        }
    }
})
