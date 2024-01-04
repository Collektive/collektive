package it.unibo.collektive.alignment

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.IntId
import it.unibo.collektive.aggregate.ops.neighbouring
import it.unibo.collektive.field.Field.Companion.hood
import it.unibo.collektive.field.minus
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager
import it.unibo.collektive.stack.Path
import kotlin.random.Random

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
            listOf("invoke.1", true, "test2.1", "test.1", "neighbouring.1", "exchange.1"),
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
    "A field should be projected when used in a body of a branch condition (issue #171)" {
        val nm = NetworkManager()
        (0..3)
            .map { IntId(it) }
            .map { NetworkImplTest(nm, it) to it }
            .map { (net, id) ->
                aggregate(id, net) {
                    val x = neighbouring(0)
                    x.neighborsCount shouldBe id.id
                    if (id.id % 2 == 0) {
                        neighbouring(1).neighborsCount shouldBe id.id / 2
                        neighbouring(1) + x
                    } else {
                        neighbouring(1).neighborsCount shouldBe (id.id - 1) / 2
                        neighbouring(1) - x
                    }
                }
            }
    }
})
