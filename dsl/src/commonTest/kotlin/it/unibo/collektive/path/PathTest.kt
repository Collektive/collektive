/*
 * Copyright (c) 2024, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.path

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import it.unibo.collektive.path.impl.PathImpl

class PathTest : StringSpec({
    "Should be able to generate 1 million different paths" {
        val seq = generateSequence(1) { it + 1 }.take(1_000_000)
        seq.count() shouldBe 1_000_000
        val res = seq.map { PathImpl.of(listOf(it)) }
        res.count() shouldBe 1_000_000
    }
})