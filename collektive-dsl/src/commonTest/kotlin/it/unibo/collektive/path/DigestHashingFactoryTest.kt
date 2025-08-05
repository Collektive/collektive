/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import it.unibo.collektive.Collektive
import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.aggregate.api.neighboring
import it.unibo.collektive.stdlib.spreading.multiGradientCast
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class DigestHashingFactoryTest {
    @OptIn(ExperimentalUuidApi::class)
    @Test
    fun `multiGradientCast with Uuid test set cast to alignedOn`() {
        val sources: Set<Uuid> = setOf(
            Uuid.parse("00000000-0000-0000-0000-000000000001"),
            Uuid.parse("00000000-0000-0000-0000-000000000002"),
            Uuid.parse("00000000-0000-0000-0000-000000000003"),
        )
        val result = Collektive.aggregate(sources.first()) {
            multiGradientCast(
                sources = sources,
                local = "data",
                metric = { neighboring(1.0) },
            )
        }
        assertEquals(3, result.result.size)
        assertEquals(sources, result.result.keys)
    }
}
