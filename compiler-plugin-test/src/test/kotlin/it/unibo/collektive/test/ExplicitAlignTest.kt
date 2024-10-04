package it.unibo.collektive.test

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.kotest.core.spec.style.FreeSpec
import it.unibo.collektive.test.util.CompileUtils.shouldCompileWith
import it.unibo.collektive.test.util.CompileUtils.warning
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@OptIn(ExperimentalCompilerApi::class)
class ExplicitAlignTest : FreeSpec({
    val aggregate = ClassName("it.unibo.collektive.aggregate.api", "Aggregate")
    val aggregateOfInt = aggregate.parameterizedBy(INT)
    val fileTemplate = FileSpec.builder("", "SingleAggregateInLoop.kt")
        .addImport(aggregate.packageName, "Aggregate")
        .build()
    val functionTemplate = FunSpec.builder("containerFun")
        .receiver(aggregateOfInt).build()
    "The `align` function" - {
        "should produce a warning when used explicitly" - {
            fileTemplate.toBuilder()
                .addFunction(
                    functionTemplate.toBuilder().addCode("align(null)").build(),
                )
                .build() shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("align"))
        }
    }
    "The `dealign` function" - {
        "should produce a warning when used explicitly" - {
            fileTemplate.toBuilder()
                .addFunction(
                    functionTemplate.toBuilder().addCode("dealign()").build(),
                )
                .build() shouldCompileWith warning(EXPECTED_WARNING_MESSAGE.format("dealign"))
        }
    }
}) {
    companion object {
        const val EXPECTED_WARNING_MESSAGE = "Warning: '%s' method should not be explicitly used"
    }
}
