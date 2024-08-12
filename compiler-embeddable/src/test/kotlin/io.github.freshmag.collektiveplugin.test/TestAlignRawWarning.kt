package io.github.freshmag.collektiveplugin.test

import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.compiler.CollektiveJVMCompiler
import it.unibo.collektive.compiler.logging.CollectingMessageCollector
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi


@OptIn(ExperimentalCompilerApi::class)
class TestAlignRawWarning: FreeSpec({
    "A single aggregate function called inside another one" - {
        val moduleName = "TestAggregateInLoop"
        val program = checkNotNull(ClassLoader.getSystemClassLoader().getResource("TestAggregateInLoop.kt")).readText()
        "should compile" - {
            val collector = CollectingMessageCollector()
            val result = CollektiveJVMCompiler.compileString(
                program,
                moduleName = moduleName,
                messageCollector = collector,
            )
            checkNotNull(result)
            collector.messages.forEach {
                println("Diagnostic at ${it.location}")
                println("Severity: ${it.severity}")
                println("Message: ${it.message}")
                println()
            }
        }
    }

}
)