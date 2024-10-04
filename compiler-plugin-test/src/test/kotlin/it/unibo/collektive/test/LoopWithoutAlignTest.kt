package it.unibo.collektive.test

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.noWarning
import it.unibo.collektive.test.util.CompileUtils.shouldCompileWith
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class LoopWithoutAlignTest : FreeSpec({
    "When being inside a loop in an Aggregate function" - {
        val aggregate = ClassName("it.unibo.collektive.aggregate.api", "Aggregate")
        val aggregateOfInt = aggregate.parameterizedBy(INT)
        val fileTemplate = FileSpec.builder("", "SingleAggregateInLoop.kt")
            .addImport(aggregate.packageName, "Aggregate")
            .addFunction(
                FunSpec.builder("exampleAggregate")
                    .receiver(aggregateOfInt)
                    .returns(Int::class)
                    .addStatement("return 0")
                    .build(),
            ).build()
        val functionTemplate = FunSpec.builder("containerFun")
            .receiver(aggregateOfInt).build()
        listOf(
            "exampleAggregate" to "exampleAggregate()",
            "neighboring" to "neighboring(0)",
        ).forEach { (functionName, functionCall) ->
            "using $functionName without a specific alignedOn" - {
                "should produce a warning" - {
                    fileTemplate.toBuilder().addFunction(
                        functionTemplate.toBuilder()
                            .beginControlFlow("for(i in 1..3)")
                            .addCode(functionCall)
                            .endControlFlow()
                            .build(),
                    ).build() shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format(functionName))
                }
            }
            "using $functionName wrapped in a specific alignedOn" - {
                "should compile without any warning" - {
                    fileTemplate.toBuilder().addFunction(
                        functionTemplate.toBuilder()
                            .beginControlFlow("for(i in 1..3)")
                            .beginControlFlow("alignedOn(%L)", 0)
                            .addCode(functionCall)
                            .endControlFlow()
                            .endControlFlow()
                            .build(),
                    ).build() shouldCompileWith noWarning
                }
            }
            "using $functionName wrapped in a specific alignedOn outside the loop" - {
                fileTemplate.toBuilder().addFunction(
                    functionTemplate.toBuilder()
                        .beginControlFlow("alignedOn(%L)", 0)
                        .beginControlFlow("for(i in 1..3)")
                        .addCode(functionCall)
                        .endControlFlow()
                        .endControlFlow()
                        .build(),

                ).build() shouldCompileWith warning(
                    EXPECTED_WARNING_MESSAGE.format(functionName),
                )
            }
            "using $functionName wrapped inside another function declaration" - {
                "should compile without any warning" - {
                    fileTemplate.toBuilder().addFunction(
                        functionTemplate.toBuilder()
                            .beginControlFlow("for(i in 1..3)")
                            .beginControlFlow("fun Aggregate<Int>.nested(): Unit")
                            .addCode(functionCall)
                            .endControlFlow()
                            .endControlFlow()
                            .build(),

                    ).build() shouldCompileWith noWarning
                }
            }
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: aggregate function '%s' called inside a loop " +
            "with no manual alignment operation"
    }
}
