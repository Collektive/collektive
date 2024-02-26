package it.unibo.collektive.alignment

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.min
import it.unibo.collektive.field.minus
import it.unibo.collektive.field.plus
import it.unibo.collektive.network.NetworkImplTest
import it.unibo.collektive.network.NetworkManager

class BranchAlignment : StringSpec({
    val id0 = 0

    "Branch alignment should work in nested functions" {
        val result = aggregate(id0) {
            val condition = true

            fun test() {
                neighboringViaExchange("test")
            }

            fun test2() {
                test()
            }
            if (condition) {
                test2()
            }
        }
        val messageFor1 = result.toSend.messagesFor(id0)
        messageFor1 shouldHaveSize 1 // 1 path of alignment
        messageFor1.values.toList() shouldBe listOf("test")
    }
    "Branch alignment should not occur in non aggregate context" {
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
        result.toSend.messagesFor(id0) shouldHaveSize 0 // 0 path of alignment
    }
    "A field should be projected when used in a body of a branch condition (issue #171)" {
        val nm = NetworkManager()
        (0..2)
            .map { NetworkImplTest(nm, it) to it }
            .map { (net, id) ->
                aggregate(id, net) {
                    val outerField = neighboringViaExchange(0)
                    outerField.neighborsCount shouldBe id
                    if (id % 2 == 0) {
                        neighboringViaExchange(1).neighborsCount shouldBe id / 2
                        neighboringViaExchange(1) + outerField
                    } else {
                        neighboringViaExchange(1).neighborsCount shouldBe (id - 1) / 2
                        neighboringViaExchange(1) - outerField
                        outerField.min(Int.MAX_VALUE)
                    }
                }
            }
    }

    fun exchangeWithThreeDevices(body: Aggregate<Int>.(Field<Int, Int>) -> Field<Int, Int>) {
        val nm = NetworkManager()
        (0..2)
            .map { NetworkImplTest(nm, it) to it }
            .map { (net, id) ->
                aggregate(id, net) {
                    exchange(0) { body(it) }
                }
            }
    }
    "A field should be projected also when the field is referenced as lambda parameter (issue #171)" {
        exchangeWithThreeDevices {
            if (localId % 2 == 0) {
                neighboringViaExchange(1) + it
            } else {
                neighboringViaExchange(1) - it
            }
        }
    }

    fun manuallyAlignedExchangeWithThreeDevices(pivot: (Int) -> Any?) =
        exchangeWithThreeDevices { field ->
            alignedOn(pivot(localId)) {
                neighboringViaExchange(1) + field
            }
        }
    "A field should be projected whenever there is an alignment operation, not just on branches (issue #171)" {
        manuallyAlignedExchangeWithThreeDevices { it % 2 == 0 }
    }
    "A field should be projected whenever there is an alignment regardless of the type," +
        " not just booleans (issue #171)" {
            manuallyAlignedExchangeWithThreeDevices { it % 2 }
        }
    "A field should be projected when it is a non-direct receiver (issue #171)" {
        exchangeWithThreeDevices {
            with(it) {
                with(localId % 2 == 0) {
                    if (this) {
                        alignedMap(neighboringViaExchange(1)) { a, b -> a + b }
                    } else {
                        alignedMap(neighboringViaExchange(1)) { a, b -> a - b }
                    }
                }
            }
        }
    }
})
