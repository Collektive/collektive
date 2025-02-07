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

const val ONE_MILLION = 1_000_000

class PathTest : StringSpec({
    "One million paths can be created" {
        generateSequence(1) { it + 1 }
            .map { FullPath(listOf(it)) }
            .take(ONE_MILLION)
            .count() shouldBe ONE_MILLION
    }
})
