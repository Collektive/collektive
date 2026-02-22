/*
 * Copyright (c) 2023-2026, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.test

import com.sun.tools.javap.Main as Javap
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.beGreaterThan
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import it.unibo.collektive.compiler.CollektiveK2JVMCompiler
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import kotlin.io.path.createTempDirectory
import kotlin.io.path.listDirectoryEntries
import org.jetbrains.kotlin.cli.common.ExitCode

class CompileStringTest : FreeSpec({
    "a simple aggregate function" - {
        val moduleName = "ScriptTest"
        val destinationFolder = createTempDirectory()
        val program = checkNotNull(ClassLoader.getSystemClassLoader().getResource("ScriptTest.kt")).readText()
        "should compile" - {
            val result = CollektiveK2JVMCompiler.compileString(
                program,
                module = moduleName,
                destinationFolder = destinationFolder,
            )
            result shouldBe ExitCode.OK
            val files = destinationFolder.listDirectoryEntries().map { it.toFile() }
            "producing some class files" {
                files.count { it.extension == "class" } should beGreaterThan(0)
            }
            val compiledFile = files.first { it.name == "$moduleName.class" }
            "bytecode with calls to the alignment method" {
                val disassembled =
                    ByteArrayOutputStream().use { outputStream ->
                        val writer = PrintWriter(outputStream)
                        Javap.run(arrayOf("-c", compiledFile.absolutePath), writer)
                        outputStream.toString()
                    }
                disassembled shouldNot beNull()
                disassembled shouldNot beBlank()
                val alignedOnCalls =
                    disassembled.lines().filter {
                        "// InterfaceMethod it/unibo/collektive/aggregate/api/Aggregate.align" in it
                    }
                alignedOnCalls.size should beGreaterThan(1)
            }
        }
    }
})
