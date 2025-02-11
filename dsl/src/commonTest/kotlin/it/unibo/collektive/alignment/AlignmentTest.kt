package it.unibo.collektive.alignment

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.operators.neighboringViaExchange
import it.unibo.collektive.aggregate.api.operators.share
import it.unibo.collektive.field.Field
import it.unibo.collektive.matchers.assertNotAligned
import it.unibo.collektive.stdlib.ints.FieldedInts.plus
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AlignmentTest {
    @Test
    fun `the alignment should be performed also for the same aggregate operation called multiple times issue 51`() {
        val result =
            aggregate(0) {
                neighboringViaExchange(10) // path -> [neighboring.1] = 10
                share(5) {
                    requireNotNull(neighboringViaExchange(20).localValue) // path -> [share.1, neighboring.2] = 20
                    it.localValue
                } // path -> [sharing.1] = 5
                neighboringViaExchange(30) // path -> [neighboring.3] = 30
                5
            }
        assertEquals(5, result.result)
        val messageFor0 = result.toSend.prepareMessageFor(0).sharedData
        assertEquals(4, messageFor0.size) // 4 paths of alignment
        assertEquals(listOf(10, 20, 5, 30), messageFor0.values.toList())
    }

    @Test
    fun `alignment must fail clearly when entries try to override each other`() {
        val exception =
            assertFailsWith<IllegalStateException> {
                aggregate(0) {
                    repeat(2) {
                        neighboringViaExchange(0)
                    }
                }
            }
        assertContains(exception.message.toString(), "Aggregate alignment clash originated at the same path:")
    }

    @Test
    fun `different alignment should be performed when a function has an aggregate parameter`() {
        val x = 0

        fun foo(agg: Aggregate<Int>) = agg.neighboringViaExchange(x)

        assertNotAligned(
            { foo(this) },
            { neighboringViaExchange(x) },
        )
    }

    @Test
    fun `overload function with different arity should not align`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)

        fun foo(
            value: Int,
            aggregate: Aggregate<Int>,
        ) = aggregate.neighboringViaExchange(value)

        assertNotAligned(
            { foo(this) },
            { foo(x, this) },
        )
    }

    @Test
    fun `overload function with different arguments order should not align`() {
        val x = 0

        fun foo(
            aggregate: Aggregate<Int>,
            value: Int,
        ) = aggregate.neighboringViaExchange(value)

        fun foo(
            value: Int,
            aggregate: Aggregate<Int>,
        ) = aggregate.neighboringViaExchange(value)

        assertNotAligned(
            { foo(this, x) },
            { foo(x, this) },
        )
    }

    @Test
    fun `non-collektive function taking collective function as argument should align the non-collective function`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)

        fun bar(
            f1: Field<Int, Int>,
            f2: Field<Int, Int>,
        ) = f1 + f2

        assertNotAligned(
            { foo(this) to foo(this) },
            { bar(foo(this), foo(this)) },
        )
    }

    @Test
    fun `different outer non-collektive functions with the same aggregate body should not align`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)

        fun bar(aggregate: Aggregate<Int>) = aggregate.neighboringViaExchange(x)

        assertNotAligned(
            { foo(this) },
            { bar(this) },
        )
    }
}
