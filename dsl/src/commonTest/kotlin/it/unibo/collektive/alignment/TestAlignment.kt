package it.unibo.collektive.alignment

import io.kotest.assertions.throwables.shouldThrowUnit
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field
import it.unibo.collektive.field.plus

class TestAlignment : StringSpec({
    "The alignment should be performed also for the same aggregate operation called multiple times (issue #51)" {
        val result =
            aggregate(0) {
                neighboringViaExchange(10) // path -> [neighboring.1] = 10
                share(0) {
                    requireNotNull(neighboringViaExchange(20).localValue) // path -> [share.1, neighboring.2] = 20
                    5
                } // path -> [sharing.1] = 5
                neighboringViaExchange(30) // path -> [neighboring.3] = 30
                5
            }

        result.result shouldBe 5
        result.toSend.messages.keys shouldHaveSize 4 // 4 paths of alignment
        result.toSend.messages.values.map { it.default } shouldContainAll listOf(10, 20, 5, 30)
    }
    "Alignment must fail clearly when entries try to override each other" {
        val exception = shouldThrowUnit<IllegalStateException> {
            aggregate(0) {
                kotlin.repeat(2) {
                    neighboringViaExchange(0)
                }
            }
        }
        exception.message shouldContain "Aggregate alignment clash by multiple aligned calls with the same path"
    }
    "Different alignment should be performed when a function has an aggregate parameter" {
        val x = 0
        fun foo(agg: Aggregate<Int>) = agg.neighboringViaExchange(x)
        val withFunction = aggregate(x) { foo(this) }
        val bare = aggregate(x) { neighboringViaExchange(x) }
        withFunction.toSend shouldNotBe bare.toSend
        withFunction.toSend.messages.size shouldBe 1
        bare.toSend.messages.size shouldBe 1
        // The program with `foo` has an additional token in the path
        withFunction.toSend.messages.keys.first().tokens().size shouldBe 3
        bare.toSend.messages.keys.first().tokens().size shouldBe 2
    }
    "Overload function with different arity should not align" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)
        fun foo(value: Int, aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(value)
        val withFunction = aggregate(x) { foo(this) }
        val bare = aggregate(x) { foo(x, this) }
        withFunction.toSend shouldNotBe bare.toSend
    }
    "Overload function with different arguments order should not align" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>, value: Int) = aggregate.neighboringViaExchange(value)
        fun foo(value: Int, aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(value)
        val withFunction = aggregate(x) { foo(this, x) }
        val bare = aggregate(x) { foo(x, this) }
        withFunction.toSend shouldNotBe bare.toSend
    }
    "Outer non-collective function taking collective function as argument should align the non-collective function" {
        val x = 0
        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)
        fun bar(f1: Field<Int, Int>, f2: Field<Int, Int>) = f1 + f2
        val withFunction = aggregate(x) { foo(this) to foo(this) }
        val bare = aggregate(x) { bar(foo(this), foo(this)) }
        withFunction.toSend shouldNotBe bare.toSend
    }
})
