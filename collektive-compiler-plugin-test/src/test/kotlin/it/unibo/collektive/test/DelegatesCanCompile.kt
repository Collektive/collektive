/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import it.unibo.collektive.test.util.CompileUtils.compileResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class DelegatesCanCompile {

    @Test
    fun `the compiler can compile delegate variables`() {
        val result = compileResource("Delegate")
        assertEquals(0, result.first.code)
        assertFalse(result.second.hasErrors())
    }
}
