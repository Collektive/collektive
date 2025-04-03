/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.alignment

import it.unibo.collektive.Collektive.Companion.aggregate
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.aggregate.api.share
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
                neighboring(10) // path -> [neighboring.1] = 10
                share(5) {
                    requireNotNull(neighboring(20).localValue) // path -> [share.1, neighboring.2] = 20
                    it.localValue
                } // path -> [sharing.1] = 5
                neighboring(30) // path -> [neighboring.3] = 30
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
                        neighboring(0)
                    }
                }
            }
        assertContains(exception.message.toString(), "Aggregate alignment clash originated at the same path:")
    }

    @Test
    fun `different alignment should be performed when a function has an aggregate parameter`() {
        val x = 0

        fun foo(agg: Aggregate<Int>) = agg.neighboring(x)

        assertNotAligned(
            { foo(this) },
            { neighboring(x) },
        )
    }

    @Test
    fun `overload function with different arity should not align`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboring(x)

        fun foo(value: Int, aggregate: Aggregate<Int>) = aggregate.neighboring(value)

        assertNotAligned(
            { foo(this) },
            { foo(x, this) },
        )
    }

    @Test
    fun `overload function with different arguments order should not align`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>, value: Int) = aggregate.neighboring(value)

        fun foo(value: Int, aggregate: Aggregate<Int>) = aggregate.neighboring(value)

        assertNotAligned(
            { foo(this, x) },
            { foo(x, this) },
        )
    }

    @Test
    fun `non-collektive function taking collective function as argument should align the non-collective function`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboring(x)

        fun bar(f1: Field<Int, Int>, f2: Field<Int, Int>) = f1 + f2

        assertNotAligned(
            { foo(this) to foo(this) },
            { bar(foo(this), foo(this)) },
        )
    }

    @Test
    fun `different outer non-collektive functions with the same aggregate body should not align`() {
        val x = 0

        fun foo(aggregate: Aggregate<Int>) = aggregate.neighboring(x)

        fun bar(aggregate: Aggregate<Int>) = aggregate.neighboring(x)

        assertNotAligned(
            { foo(this) },
            { bar(this) },
        )
    }
}
