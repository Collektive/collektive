package it.unibo.collektive.codgen.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.asTypeVariableName
import kotlin.math.pow
import kotlin.reflect.KCallable
import kotlin.reflect.KParameter

private val FIELD_INTERFACE = ClassName("it.unibo.collektive.field", "Field")
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
                    FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, parameter.type)
                )
                false -> parameter
            }
        }
    }
}

internal fun KCallable<*>.hasExtensionReceiver(): Boolean =
    parameters.isNotEmpty() && parameters.first().kind == KParameter.Kind.EXTENSION_RECEIVER

internal fun generateFunctionWithExtensionReceiver(callable: KCallable<*>, paramList: List<ParameterSpec>): FunSpec {
    val extensionReceiver = paramList.first()
    val functionParameters = paramList.drop(1)
    return FunSpec.builder(callable.name).apply {
        addTypeVariable(ID_BOUNDED_TYPE)
        addTypeVariables(callable.typeParameters.map { it.asTypeVariableName() })
        addParameters(functionParameters)
        receiver(extensionReceiver.type)
        returns(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, callable.returnType.asTypeName()))
        when (paramList.size) {
            0 -> addStatement("return map { it.${callable.name}() }")
            else -> {
                val firstFieldParameter = paramList.first { it.type.toString().contains("Field") }
                val otherParameters = paramList.filter { it != firstFieldParameter }
                val arguments = otherParameters.map {
                    when (it.type.toString().contains("Field")) {
                        true -> "${it.name}[id]"
                        false -> it.name
                    }
                }
                addStatement("Field.checkAligned(this, ${functionParameters.joinToString(separator = ", ") { it.name }})")
                addStatement(
                    """
                        return $firstFieldParameter.mapWithId { id, receiver ->
                        receiver.${callable.name}(${arguments.joinToString(separator = ", ")})
                        }
                    """.trimIndent()
                )
            }
        }
    }.build()
}

internal fun generatePlainFunction(callable: KCallable<*>, paramList: List<ParameterSpec>): FunSpec {
    return FunSpec.builder(callable.name).apply {
        addTypeVariable(ID_BOUNDED_TYPE)
        addTypeVariables(callable.typeParameters.map { it.asTypeVariableName() })
        addParameters(paramList)
        returns(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, callable.returnType.asTypeName()))
        when (paramList.size) {
            0 -> addStatement("return map { it.${callable.name}() }")
            else -> {
                val firstFieldParameter = paramList.first { it.type.toString().contains("Field") }
                val otherParameters = paramList.filter { it != firstFieldParameter }
                val arguments = otherParameters.map {
                    when (it.type.toString().contains("Field")) {
                        true -> "${it.name}[id]"
                        false -> it.name
                    }
                }
                addStatement("Field.checkAligned(this, ${paramList.joinToString(separator = ", ") { it.name }})")
                addStatement(
                    """
                        return $firstFieldParameter.mapWithId { id, receiver ->
                        receiver.${callable.name}(${arguments.joinToString(separator = ", ")})
                        }
                    """.trimIndent()
                )
            }
        }
    }.build()
}

internal fun generateFunctions(origin: KCallable<*>): List<FunSpec> {
    val functionArguments = origin.parameters.map {
        ParameterSpec(it.name ?: "<receiver>", it.type.asTypeName())
    }
//    val hasExtensionReceiver = origin.hasExtensionReceiver()

    return parameterCombinations(functionArguments).map { paramList ->
        when (origin.hasExtensionReceiver()) {
            true -> generateFunctionWithExtensionReceiver(origin, paramList)
            false -> generatePlainFunction(origin, paramList)
        }
//
//
//        val functionReceiver = paramList.first().type
//        val functionParameters = paramList.drop(1)
//        FunSpec.builder(origin.name).apply {
//            addTypeVariable(ID_BOUNDED_TYPE)
//            addTypeVariables(origin.typeParameters.map { it.asTypeVariableName() })
//            addParameters(functionParameters)
//            receiver(functionReceiver)
//            returns(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, origin.returnType.asTypeName()))
//            when (paramList.size) {
//                0 -> addStatement("return map { it.$functionName() }")
//                else -> {
//                    val arguments = paramList.map {
//                        when (it.type == FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, it.type)) {
//                            true -> "${it.name}[id]"
//                            false -> it.name
//                        }
//                    }
//                    addStatement("Field.checkAligned(this, ${functionParameters.joinToString(separator = ", ") { it.name }})")
//                    addStatement(
//                        """
//                            return mapWithId { id, receiver ->
//                            receiver.$functionName(${arguments.joinToString(separator = ", ")})
//                            }
//                        """.trimIndent()
//                    )
//                }
//            }
//        }.build()
    }
}
