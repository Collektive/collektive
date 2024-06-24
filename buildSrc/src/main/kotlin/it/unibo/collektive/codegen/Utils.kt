package it.unibo.collektive.codegen

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import it.unibo.collektive.field.Field
import kotlin.math.pow
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance

private val FIELD_INTERFACE = Field::class.asClassName()
private val FIELD_COMPANION = Field.Companion::class.asClassName()
private val CHECK_ALIGNED = FIELD_COMPANION.member("checkAligned")
private val FIELD_MAP = FIELD_INTERFACE.member("map")
private val FIELD_MAP_WITH_ID = FIELD_INTERFACE.member("mapWithId")
private val ID_BOUNDED_TYPE = TypeVariableName("ID", Any::class.asTypeName())
private val operatorNotReturningField = listOf("compareTo", "contains")

internal fun ParameterSpec.isField() = type.toString().contains("Field")
private fun KCallable<*>.isProperty() = this is KProperty<*>

val specializedArrayTypes = setOf(
    IntArray::class,
    DoubleArray::class,
    LongArray::class,
    FloatArray::class,
    ShortArray::class,
    ByteArray::class,
    CharArray::class,
    BooleanArray::class
)

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
internal fun FunSpec.Builder.noArgumentFunction(callable: KCallable<*>) {
    val baseCall = "return·%N·{·it.${callable.name}"
    when {
        callable.isProperty() -> addStatement("$baseCall·}", FIELD_MAP)
        callable.typeParameters.isEmpty() -> addStatement("$baseCall()·}", FIELD_MAP)
        else -> addStatement("$baseCall<${callable.typeParameters.joinToString { it.name }}>()·}", FIELD_MAP)
    }
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
    val namedParameter = if (arguments.any { it.contains("id") }) "id" else "_"
    val toCheckAligned = parameters.filter { it.isField() }
    if (toCheckAligned.size > 1) {
        addStatement("%M(${toCheckAligned.joinToString(separator = ",·") { it.name }})", CHECK_ALIGNED)
    }
    when (callable.isProperty()) {
        true ->
            addStatement(
                "return ${firstFieldParameter.name}.%N·{·$namedParameter,·receiver·->·receiver.%N }",
                FIELD_MAP_WITH_ID,
                callable.name,
            )

        else ->
            addStatement(
                "return ${firstFieldParameter.name}.%N·{·$namedParameter,·receiver·->·receiver.%N(${
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
        if (it == candidateParameter) return@map "receiver"
        when (it.type.toString().contains("Field")) {
            true -> "${it.name}[id]"
            false -> it.name
        }
    }
    val namedParameter = if (arguments.any { it.contains("id") }) "id" else "_"
    val toCheckAligned = parameters.filter { it.isField() }
    if (toCheckAligned.size > 1) {
        addStatement("%M(${toCheckAligned.joinToString(separator = ",·") { it.name }})", CHECK_ALIGNED)
    }
    when (callable.isProperty()) {
        true ->
            addStatement(
                "return ${candidateParameter.name}.%N·{·$namedParameter,·receiver·->·this.%N }",
                FIELD_MAP_WITH_ID,
                callable.name,
            )

        else -> {
            addStatement(
                "return ${candidateParameter.name}.%N·{·$namedParameter,·receiver·->·this.%N(${
                    arguments.joinToString(
                        separator = ",·",
                    )
                })·}",
                FIELD_MAP_WITH_ID,
                callable.name,
            )
        }
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

internal fun generateFunction(callable: KCallable<*>, paramList: List<ParameterSpec>): FunSpec? {
    /*
     * KotlinPoet does not include the variance on return types:
     * if a variant type is returned, then omit the return type annotation;
     * remove when https://github.com/square/kotlinpoet/issues/1933 is resolved.
     */
    val potentialBoundedTypeArguments = callable.returnType.arguments.toMutableList()
    var thereIsNoVariantType = true
    while (thereIsNoVariantType && potentialBoundedTypeArguments.isNotEmpty()) {
        val typeProjection = potentialBoundedTypeArguments.removeLast()
        if (typeProjection.variance != KVariance.INVARIANT) {
            thereIsNoVariantType = false
        }
        potentialBoundedTypeArguments.addAll(typeProjection.type?.arguments.orEmpty())
    }
    return when {
        thereIsNoVariantType -> {
            FunSpec.builder(callable.name).apply {
                // Retain suspend
                if (callable.isSuspend) {
                    addModifiers(KModifier.SUSPEND)
                }
                when (callable) {
                    is KFunction<*> -> {
                        // If the callable is an operator, add the operator modifier to the function
                        if (callable.isOperator && callable.name !in operatorNotReturningField) {
                            addModifiers(KModifier.OPERATOR)
                        }
                        // Retain infix
                        if (callable.isInfix) {
                            addModifiers(KModifier.INFIX)
                        }
                        // Retain inline
                        fun hasCrossinlinedParameters() = paramList.any { it.modifiers.contains(KModifier.CROSSINLINE) }
                        fun hasReifiedTypeParameters() = callable.typeParameters.any { it.isReified }
                        if (callable.isInline && (hasCrossinlinedParameters() || hasReifiedTypeParameters())) {
                            addModifiers(KModifier.INLINE)
                        }
                    }
                }
                // Add type variables to the function definition by recursively getting all type variables from the parameters
                // The .toSet() call is to remove duplicates
                val declaredTypeVariables = callable.typeParameters.map { it.toTypeVariableName() }
                addTypeVariables(declaredTypeVariables)
                val declaredTypeVariableNames = declaredTypeVariables.map { it.name }
                val typeVariablesInParameters = paramList
                    .map { it.type }
                    .flatMap { it.getAllTypeVariables() }
                    .distinct()
                    .filterNot { it.name in declaredTypeVariableNames }
                addTypeVariables(typeVariablesInParameters)
                // Remove the `this` parameter since, when present, it is the extension receiver
                addParameters(paramList.filter { it.name != "this" })
                // Generate always function with extension receiver
                receiver(paramList.first().type)
                // Add the JavaName annotation preventing JVM name clashes
                val typesRepr = paramList
                    .joinToString("_and_") {
                        it.type.toString()
                            .replace("kotlin.", "")
                            .replace("kotlinx.", "")
                            .replace("it.unibo.collektive.`field`.Field", "Field")
                    }
                    .replace(".", "_")
                    .replace("<", "_of_")
                    .replace(">", "_end")
                    .replace(", ", "_and_")
                    .replace("*", "wildcard")
                    .replace("?", "_nullable")
                    .replace("Field_of_ID_and_", "Field_of_")
                addAnnotation(
                    AnnotationSpec.builder(JvmName::class)
                        .addMember("%S", "${callable.name}_with_$typesRepr")
                        .build(),
                )
                // Always return a Field parametrized by the ID type and the return type of the callable
                val returnType = callable.returnType.toTypeNameWithRecurringGenericSupport()
                returns(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, returnType))
                when (paramList.size) {
                    1 -> noArgumentFunction(callable)
                    else -> when (paramList.first().isField()) {
                        true -> addBodyForFieldReceiverFunction(callable, paramList)
                        false -> addBodyForNonFieldReceiverFunction(callable, paramList)
                    }
                }
            }.build()
        }
        else -> null
    }
}

internal fun ParameterSpec.isFunctionTyoe(): Boolean = type.toString()
    .matches(Regex("^(kotlin\\.)?Function\\d+.*"))

internal fun KParameter.isFunctionType(): Boolean = (type.classifier as? KClass<*>)?.qualifiedName
    ?.startsWith("kotlin.Function")
    ?: false

internal fun generateFunctions(origin: KCallable<*>): List<FunSpec> {
    val functionArguments: List<ParameterSpec> = origin.parameters.map { parameter: KParameter ->
        val typeName = parameter.type.toTypeNameWithRecurringGenericSupport()
        ParameterSpec(
            name = parameter.name ?: "this",
            type = typeName.copy(annotations = emptyList()),
            modifiers = when {
                origin is KFunction<*> && origin.isInline && parameter.isFunctionType() ->
                    listOf(KModifier.CROSSINLINE)
                else -> emptyList()
            }
        )
    }
    return parameterCombinations(functionArguments).mapNotNull { paramList ->
        generateFunction(origin, paramList)
    }
}

internal fun generatePrimitivesFile(origin: List<KCallable<*>>, packageName: String, fileName: String): FileSpec {
    return FileSpec.builder(packageName, fileName).apply {
        addFileComment("This file is auto-generated by the Collektive code generator. Do not edit it manually.")
        addAnnotation(
            AnnotationSpec.builder(Suppress::class)
                .addMember(
                    "%S,%S",
                    "all",
                    "ktlint"
                )
                .build(),
        )
        val objectContainer = TypeSpec.objectBuilder(fileName).apply {
            origin.flatMap { generateFunctions(it) }.forEach {
                addFunction(it)
            }
        }.build()
        addType(objectContainer)
    }.build()
}

val autoConversions = mapOf(
    "java.lang.Appendable" to "kotlin.text.Appendable",
    "java.util.Comparator" to "kotlin.Comparator",
    "java.util.HashSet" to "kotlin.collections.HashSet",
).mapValues { (_, kotlinClass) -> ClassName.bestGuess(kotlinClass) }

internal fun KVariance?.toKModifier() = when (this) {
    null -> null
    KVariance.INVARIANT -> null
    KVariance.IN -> KModifier.IN
    KVariance.OUT -> KModifier.OUT
}

internal fun KTypeParameter.toTypeVariableName(
    recurryingTypeArguments: Set<KTypeParameter> = emptySet()
): TypeVariableName =
    when {
        this in recurryingTypeArguments -> TypeVariableName(
            name = name,
            variance = variance.toKModifier()
        ).copy(reified = isReified)
        else -> {
            val unbound = TypeVariableName(name)
            val upperBounds = upperBounds
                .filterNot { it.isMarkedNullable && it.classifier == Any::class }
                .map {
                    it.toTypeNameWithRecurringGenericSupport(recurryingTypeArguments + this)
                }
            unbound.copy(
                bounds = upperBounds,
                reified = isReified
            )
        }
    }

internal fun KTypeProjection.toTypeNameWithRecurringGenericSupport(
    recurringTypeArguments: Set<KTypeParameter> = emptySet()
): TypeName {
    val result = type.toTypeNameWithRecurringGenericSupport(recurringTypeArguments)
    return when (result) {
        is TypeVariableName ->
            TypeVariableName(name = result.name, variance = variance.toKModifier()).copy(
                nullable = result.isNullable,
                annotations = result.annotations,
                reified = result.isReified
            )
        else -> result
    }
}

internal fun KType?.toTypeNameWithRecurringGenericSupport(
    recurringTypeArguments: Set<KTypeParameter> = emptySet()
): TypeName {
    if (this == null) {
        return STAR
    }
    val classifier = classifier
    fun ClassName.parameterized(): TypeName = run {
        when {
            arguments.isEmpty() -> this
            specializedArrayTypes.contains(classifier) -> this // No type arguments expected for class '*Array'.
            else -> parameterizedBy(
                arguments.map {
                    it.toTypeNameWithRecurringGenericSupport(recurringTypeArguments)

                }
            )
        }
    }
    return when(classifier) {
        is KClass<*> -> {
            val qualifiedName = classifier.qualifiedName
            when (qualifiedName) {
                null -> error("Cannot generate types for anonymous class $classifier")
                in autoConversions -> {
                    autoConversions.getValue(qualifiedName).parameterized()
                }
                else -> {
                    ClassName.bestGuess(qualifiedName).parameterized().copy(nullable = isMarkedNullable)
                }
            }
        }
        is KTypeParameter -> classifier.toTypeVariableName(recurringTypeArguments).copy(nullable = isMarkedNullable)
        else -> asTypeName()
    }
}
