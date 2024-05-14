package it.unibo.collektive.test

import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.ints.beGreaterThan
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.should
import io.kotest.matchers.shouldNot
import io.kotest.matchers.string.beBlank
import it.unibo.collektive.compiler.CollektiveJVMCompiler
import it.unibo.collektive.compiler.util.jvmOutputDirectory
import java.io.ByteArrayOutputStream
import java.io.PrintWriter
import com.sun.tools.javap.Main as Javap

class TestCompileString : FreeSpec({
    "a simple aggregate function" - {
        val moduleName = "TestScript"
        val program = checkNotNull(ClassLoader.getSystemClassLoader().getResource("TestScript.kt")).readText()
        "should compile" - {
            val result = CollektiveJVMCompiler.compileString(program, moduleName = moduleName)
            checkNotNull(result)
            val files = result.jvmOutputDirectory().listFiles()
            checkNotNull(files)
            "producing some class files" {
                files.count { it.extension == "class" } should beGreaterThan(0)
            }
            val compiledFile = files.first { it.name == "$moduleName.class" }
            "bytecode with calls to the alignment method" {
                val disassembled = ByteArrayOutputStream().use { outputStream ->
                    val writer = PrintWriter(outputStream)
                    Javap.run(arrayOf("-c", compiledFile.absolutePath), writer)
                    outputStream.toString()
                }
                disassembled shouldNot beNull()
                disassembled shouldNot beBlank()
                val alignedOnCalls = disassembled.lines().filter {
                    "// InterfaceMethod it/unibo/collektive/aggregate/api/Aggregate.alignRaw:" in it
                }
                alignedOnCalls.size should beGreaterThan(1)
            }
        }
    }
})
