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
import kotlin.test.assertTrue

class BranchesCannotReturnFieldTest {

    private val String.doesNotCompileBecauseBranchesReturnField get(): Unit {
        assertTrue(compileResource(this).second.hasErrors())
    }

    @Test
    fun `the compiler throws error on branches returning fields`() {
        "BrokenBranch".doesNotCompileBecauseBranchesReturnField
    }

    @Test
    fun `the compiler throws error on branches returning fields inside lambdas`() {
        "BrokenBranch2".doesNotCompileBecauseBranchesReturnField
    }

    @Test
    fun `even when the return type is Any, if one branch returns a field the compiler raises errors`() {
        "BrokenBranch3".doesNotCompileBecauseBranchesReturnField
    }
}
