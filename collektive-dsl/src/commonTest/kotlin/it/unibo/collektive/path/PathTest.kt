/*
 * Copyright (c) 2024-2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.AlignmentClashException
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

const val ONE_MILLION = 1_000_000

class PathTest {
    @Test
    fun `one million paths can be created`() {
        assertEquals(
            ONE_MILLION,
            generateSequence(1) { it + 1 }
                .map { FullPath(listOf(it)) }
                .take(ONE_MILLION)
                .count(),
        )
    }

    @Test
    fun `alignment errors with full paths are decently readable`() {
        @Suppress("AGGREGATE_FUNCTION_INSIDE_ITERATION")
        fun Aggregate<Int>.baz() = listOf(1, 2).map { neighboring(it) }
        fun Aggregate<Int>.bar() = baz()
        fun Aggregate<Int>.foo() = bar()
        val error = assertFailsWith<AlignmentClashException> {
            Collektive.aggregate(0, pathFactory = FullPathFactory) {
                foo()
            }
        }
        assertEquals(6, error.path.toMultilineString().lines().count())
    }
}
