package it.unibo.collektive.test

import io.github.subjekt.Subjekt.subjekt
import io.github.subjekt.generators.FilesGenerator.toTempFiles
import it.unibo.collektive.test.util.CompileUtils.formsOfIteration
import it.unibo.collektive.test.util.CompileUtils.`iteration with warning`
import it.unibo.collektive.test.util.CompileUtils.`iteration without warning`
import it.unibo.collektive.test.util.CompileUtils.testedAggregateFunctions
import kotlin.test.Test

class IterationWithoutAlign {
    private val testSubjects =
        subjekt {
            addSource("src/test/resources/subjekt/IterationWithAggregate.yaml")
        }.toTempFiles()

    @Test
    fun `test iterations without align`() {
        for (functionCall in testedAggregateFunctions) {
            val functionName = functionCall.substringBefore("(")
            for ((iteration, _) in formsOfIteration) {
                with(testSubjects) {
                    `iteration with warning`("Iteration", functionName, iteration, expectedWarn(functionName))
                    `iteration without warning`("IterationAlign", functionName, iteration)
                    `iteration with warning`("IterationExtAlign", functionName, iteration, expectedWarn(functionName))
                    `iteration without warning`("IterationWithNestedFun", functionName, iteration)
                    `iteration without warning`("OutsideAggregate", functionName, iteration)
                }
            }
        }
    }

    private companion object {
        private fun expectedWarn(functionName: String): String =
            """
            Aggregate function '$functionName' has been called inside a loop construct without explicit alignment.
            The same path may generate interactions more than once, leading to ambiguous alignment.
            
            Consider to wrap the function into the 'alignedOn' method with a unique element.
            """.trimIndent()
    }
}
