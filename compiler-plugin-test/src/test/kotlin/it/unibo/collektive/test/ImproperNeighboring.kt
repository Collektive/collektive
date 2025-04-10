/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toTempFiles
import it.unibo.collektive.test.util.CompileUtils.getTestingProgram
import it.unibo.collektive.test.util.CompileUtils.noWarning
import kotlin.test.Test

class ImproperNeighboring {
    @Test
    fun `mapping the result of a neighboring should not throw a loop warning`() {
        val testSubjects = subjekt {
            addSource("src/test/resources/subjekt/ImproperNeighboring.yaml")
        }.toTempFiles()
        val subjectName = "ProperUseNeighboringMapping"
        val code = testSubjects.getTestingProgram(subjectName)
        code shouldCompileWith noWarning
    }
}
