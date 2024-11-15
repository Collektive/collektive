package it.unibo.collektive.alignment

import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field
import it.unibo.collektive.matchers.acProgram
import it.unibo.collektive.matchers.alignWith
import it.unibo.collektive.stdlib.ints.FieldedInts.plus

class AlignmentTest : StringSpec({

    "The alignment should be performed also for the same aggregate operation called multiple times (issue #51)" {
        val result = aggregate(0) {
            neighboringViaExchange(10) // path -> [neighboring.1] = 10
            share(0) {
                requireNotNull(neighboringViaExchange(20).localValue) // path -> [share.1, neighboring.2] = 20
                5
            } // path -> [sharing.1] = 5
            neighboringViaExchange(30) // path -> [neighboring.3] = 30
            5
        }
        result.result shouldBe 5
        val messageFor0 = result.toSend.messagesFor(0)
        messageFor0 shouldHaveSize 4 // 4 paths of alignment
        messageFor0.values.toList() shouldBe listOf(10, 20, 5, 30)
    }

    "Alignment must fail clearly when entries try to override each other" {
        val exception = shouldThrowUnit<IllegalStateException> {
            aggregate(0) {
                repeat(2) {
                    neighboringViaExchange(0)
                }
            }
        }
        exception.message shouldContain
            "Aggregate alignment clash originated at the same path:"
    }

    "Different alignment should be performed when a function has an aggregate parameter" {
        val x = 0
        fun foo(agg: Aggregate<Int>) = agg.neighboringViaExchange(x)
        acProgram { foo(this) } shouldNot alignWith { neighboringViaExchange(x) }
    }

    "Overload function with different arity should not align" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)
        fun foo(value: Int, aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(value)
        acProgram { foo(this) } shouldNot alignWith { foo(x, this) }
    }

    "Overload function with different arguments order should not align" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>, value: Int) = aggregate.neighboringViaExchange(value)
        fun foo(value: Int, aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(value)
        acProgram { foo(this, x) } shouldNot alignWith { foo(x, this) }
    }

    "Outer non-collektive function taking collective function as argument should align the non-collective function" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)
        fun bar(f1: Field<Int, Int>, f2: Field<Int, Int>) = f1 + f2
        acProgram { foo(this) to foo(this) } shouldNot alignWith { bar(foo(this), foo(this)) }
    }

    "Different outer non-collektive functions with the same aggregate body should not align" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)
        fun bar(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)
        acProgram { foo(this) } shouldNot alignWith { bar(this) }
    }
})
