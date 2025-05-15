/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import it.unibo.collektive.compiler.CollektiveK2JVMCompiler
import it.unibo.collektive.compiler.logging.CollectingMessageCollector
import kotlin.io.path.createTempFile
import kotlin.io.path.writeText
import kotlin.test.Test
import kotlin.test.assertTrue

class BranchesCannotReturnFieldTest {

    private val String.doesNotCompileBecauseBranchesReturnField get(): Unit {
        val collector = CollectingMessageCollector()
        val file = createTempFile(suffix = ".kt")
        file.writeText(ClassLoader.getSystemResource("it/unibo/collektive/test/$this.kt").readText())
        CollektiveK2JVMCompiler.compile(file.toFile(), collector)
        assertTrue(collector.hasErrors())
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
