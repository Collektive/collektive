package it.unibo.collektive.codgen.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.asTypeVariableName
import kotlin.math.pow
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction

private val FIELD_INTERFACE = ClassName("it.unibo.collektive.field", "Field")
private val FIELD_COMPANION = ClassName("it.unibo.collektive.field.Field", "Companion")
private val CHECK_ALIGNED = FIELD_COMPANION.member("checkAligned")
private val ANY_TYPE = ClassName("kotlin", "Any")
private val ID_BOUNDED_TYPE = TypeVariableName("ID", ANY_TYPE)

internal fun decimalToBinaryArray(decimal: Int, size: Int): List<Boolean> {
    require(size <= Int.SIZE_BITS) { "Size must be less than or equal to ${Int.SIZE_BITS}" }
    return (0..Int.SIZE_BITS).take(size).map { (decimal shr it) and 1 == 1 }
}

internal fun parameterCombinations(parameters: List<ParameterSpec>): List<List<ParameterSpec>> {
    val parametersSize = 2.0.pow(parameters.size).toInt()
    val decimals = parameters.size
    val combinationMap = (1 until parametersSize).associateWith { i -> decimalToBinaryArray(i, decimals) }

    return combinationMap.map { (_, binaryArray) ->
        parameters.zip(binaryArray).map { (parameter, isField) ->
            when (isField) {
                true -> ParameterSpec(
                    parameter.name,
                    FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, parameter.type),
                )

                false -> parameter
            }
        }
    }
}

internal fun generateFunction(callable: KCallable<*>, paramList: List<ParameterSpec>): FunSpec {
    return FunSpec.builder(callable.name).apply {
        addTypeVariable(ID_BOUNDED_TYPE)
        addTypeVariables(callable.typeParameters.map { it.asTypeVariableName() })
        addParameters(paramList.filter { it.name != "this" })
        receiver(paramList.first().type)
        when (callable) {
            is KFunction<*> -> if (callable.isOperator) addModifiers(KModifier.OPERATOR)
        }
        returns(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, callable.returnType.asTypeName()))
        when (paramList.size) {
            1 -> addStatement("return·map·{·it.${callable.name}()·}")
            else -> {
                val firstFieldParameter = paramList.first { it.type.toString().contains("Field") }
                val otherParameters = paramList.filter { it != firstFieldParameter }
                val arguments = otherParameters.map {
                    when (it.type.toString().contains("Field")) {
                        true -> "${it.name}[id]"
                        false -> it.name
                    }
                }
                val toCheckAligned = paramList.filter { it.type.toString().contains("Field") }
                if (toCheckAligned.size > 1) {
                    addStatement("%M(${toCheckAligned.joinToString(separator = ",·") { it.name }})", CHECK_ALIGNED)
                }
                addStatement(
                    "return ${firstFieldParameter.name}.mapWithId·{·id,·receiver·->·receiver.%N(${
                        arguments.joinToString(
                            separator = ",·",
                        )
                    })·}",
                    callable.name,
                )
            }
        }
    }.build()
}

internal fun generateFunctions(origin: KCallable<*>): List<FunSpec> {
    val functionArguments = origin.parameters.map {
        ParameterSpec(it.name ?: "this", it.type.asTypeName())
    }

    return parameterCombinations(functionArguments).map { paramList ->
        generateFunction(origin, paramList)
    }
}

internal fun generatePrimitivesFile(origin: List<KCallable<*>>): FileSpec {
    return FileSpec.builder("it.unibo.collektive.codgen", "FieldToFieldOperations").apply {
        origin.flatMap { generateFunctions(it) }.forEach { addFunction(it) }
    }.build()
}
