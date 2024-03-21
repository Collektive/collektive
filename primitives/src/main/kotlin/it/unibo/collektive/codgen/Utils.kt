package it.unibo.collektive.codgen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import it.unibo.collektive.field.Field
import kotlin.math.pow
import kotlin.reflect.KCallable
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

private val FIELD_INTERFACE = Field::class.asClassName()
private val FIELD_COMPANION = Field.Companion::class.asClassName()
private val CHECK_ALIGNED = FIELD_COMPANION.member("checkAligned")
private val FIELD_MAP = FIELD_INTERFACE.member("map")
private val FIELD_MAP_WITH_ID = FIELD_INTERFACE.member("mapWithId")
private val ID_BOUNDED_TYPE = TypeVariableName("ID", Any::class.asTypeName())
private val operatorNotReturningField = listOf("compareTo", "contains")

internal fun ParameterSpec.isField() = type.toString().contains("Field")
private fun KCallable<*>.isProperty() = this is KProperty<*>

/**
 * Given a list of parameters, returns a list of all possible combinations of parameters where each parameter is
 * replaced by a `Field` of the same type.
 * The function drop the first combination where all parameters are not `Field`.
 */
internal fun parameterCombinations(parameters: List<ParameterSpec>): List<List<ParameterSpec>> {
    fun decimalToBinaryArray(decimal: Int, size: Int): List<Boolean> {
        require(size <= Int.SIZE_BITS) { "Size must be less than or equal to ${Int.SIZE_BITS}" }
        return (0..Int.SIZE_BITS).take(size).map { (decimal shr it) and 1 == 1 }
    }

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

/**
 * Generate the body for function where the extension receiver is a `Field` and the function has no arguments.
 */
internal fun FunSpec.Builder.noArgumentFunction(callable: KCallable<*>) =
    when (callable.isProperty()) {
        true -> addStatement("return·%N·{·it.${callable.name}·}", FIELD_MAP)
        else -> addStatement("return·%N·{·it.${callable.name}()·}", FIELD_MAP)
    }

/**
 * Generate the body for function where the extension receiver is a `Field`.
 * The strategy is to call the `mapWithId` function on the receiver and use the `id` on arguments that are `Field`,
 * and the argument itself otherwise.
 */
internal fun FunSpec.Builder.addBodyForFieldReceiverFunction(callable: KCallable<*>, parameters: List<ParameterSpec>) {
    val functionParameters = parameters.drop(1)
    val firstFieldParameter = parameters.first { it.isField() }
    val arguments = functionParameters.map {
        when (it.type.toString().contains("Field")) {
            true -> "${it.name}[id]"
            false -> it.name
        }
    }
    val toCheckAligned = functionParameters.filter { it.isField() }
    if (toCheckAligned.size > 1) {
        addStatement("%M(${toCheckAligned.joinToString(separator = ",·") { it.name }})", CHECK_ALIGNED)
    }
    when (callable.isProperty()) {
        true ->
            addStatement(
                "return ${firstFieldParameter.name}.%N·{·id,·receiver·->·receiver.%N }",
                FIELD_MAP_WITH_ID,
                callable.name,
            )

        else ->
            addStatement(
                "return ${firstFieldParameter.name}.%N·{·id,·receiver·->·receiver.%N(${
                    arguments.joinToString(
                        separator = ",·",
                    )
                })·}",
                FIELD_MAP_WITH_ID,
                callable.name,
            )
    }
}

/**
 * Generate the body for function where the extension receiver is not a `Field`.
 * The strategy is to find the first `Field` parameter and use it as the receiver for the `mapWithId` function.
 * When the first `Field` parameter is found, we need to replace it with the `receiver` parameter when calling the
 * original function.
 *
 * Example:
 * ```
 * fun <ID : Any, T, E> Int.foo(arg1: Field<ID, T>, arg2: T, arg3: E): Field<ID, T> =
 *    arg1.mapWithId { id, receiver -> this.foo(receiver, arg2, arg3) }
 * ```
 *
 * In this example, `arg1` is the first `Field` parameter, so it is used as the receiver for the `mapWithId` function.
 */
internal fun FunSpec.Builder.addBodyForNonFieldReceiverFunction(
    callable: KCallable<*>,
    parameters: List<ParameterSpec>,
) {
    val functionParameters = parameters.drop(1)
    val candidateParameter = functionParameters.first { it.isField() }
    val arguments = functionParameters.map {
        if (it == candidateParameter) {
            return@map "receiver"
        }
        when (it.type.toString().contains("Field")) {
            true -> "${it.name}[id]"
            false -> it.name
        }
    }
    val toCheckAligned = functionParameters.filter { it.isField() }
    if (toCheckAligned.size > 1) {
        addStatement("%M(${toCheckAligned.joinToString(separator = ",·") { it.name }})", CHECK_ALIGNED)
    }
    when (callable.isProperty()) {
        true ->
            addStatement(
                "return ${candidateParameter.name}.%N·{·id,·receiver·->·this.%N }",
                FIELD_MAP_WITH_ID,
                callable.name,
            )

        else ->
            addStatement(
                "return ${candidateParameter.name}.%N·{·id,·receiver·->·this.%N(${
                    arguments.joinToString(
                        separator = ",·",
                    )
                })·}",
                FIELD_MAP_WITH_ID,
                callable.name,
            )
    }
}

/**
 * Given a type, returns a list of all generic types defined in it recursively.
 * Examples:
 * - For `Field<ID, Array<T>>`, it returns `ID` and `T`.
 * - For `Map<ID, Pair<E, F>>`, it returns `ID`, `E`, and F`.
 * - For `Field<ID, Field<ID, T>>`, it returns `ID` and `T`.
 * - For `Field<ID, Map<T, Pair<A, B>>` it returns `ID`, `T`, `A`, and `B`.
 *
 * Since this function is used to generate the type variables for the generated functions,
 * the variance of the type variables is not considered since kotlin does not allow variance
 * in type variables for functions.
 */
internal fun TypeName.getAllTypeVariables(): List<TypeVariableName> {
    return when (this) {
        is TypeVariableName -> listOf(TypeVariableName(this.name, this.bounds, null))
        is ParameterizedTypeName -> typeArguments.flatMap { it.getAllTypeVariables() }
        else -> emptyList()
    }
}

internal fun generateFunction(callable: KCallable<*>, paramList: List<ParameterSpec>): FunSpec =
    FunSpec.builder(callable.name).apply {
        // Add type variables to the function definition by recursively getting all type variables from the parameters
        // The .toSet() call is to remove duplicates
        addTypeVariables(paramList.map { it.type }.flatMap { it.getAllTypeVariables() }.toSet())
        // Remove the `this` parameter since, when present, it is the extension receiver
        addParameters(paramList.filter { it.name != "this" })
        // Generate always function with extension receiver
        receiver(paramList.first().type)
        // If the callable is an operator, add the operator modifier to the function
        when (callable) {
            is KFunction<*> -> if (callable.isOperator && callable.name !in operatorNotReturningField) {
                addModifiers(KModifier.OPERATOR)
            }
        }
        // Always return a Field parametrized by the ID type and the return type of the callable
        returns(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, callable.returnType.asTypeName()))
        when (paramList.size) {
            1 -> noArgumentFunction(callable)
            else -> when (paramList.first().isField()) {
                true -> addBodyForFieldReceiverFunction(callable, paramList)
                false -> addBodyForNonFieldReceiverFunction(callable, paramList)
            }
        }
    }.build()

internal fun generateFunctions(origin: KCallable<*>): List<FunSpec> {
    val functionArguments = origin.parameters.map {
        ParameterSpec(it.name ?: "this", it.type.asTypeName().copy(annotations = emptyList()))
    }

    return parameterCombinations(functionArguments).map { paramList ->
        generateFunction(origin, paramList)
    }
}

internal fun generatePrimitivesFile(origin: List<KCallable<*>>, packageName: String, fileName: String): FileSpec {
    return FileSpec.builder(packageName, fileName).apply {
        addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember(
                    "%S,·%S,·%S,·%S",
                    "TooManyFunctions",
                    "UndocumentedPublicFunction",
                    "FunctionParameterNaming",
                    "FunctionNaming",
                )
                .build(),
        )
        origin.flatMap { generateFunctions(it) }.forEach { addFunction(it) }
    }.build()
}
