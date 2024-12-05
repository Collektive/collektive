package it.unibo.collektive.codegen.utils

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import it.unibo.collektive.field.Field
import it.unibo.collektive.codegen.utils.KotlinPoetUtils.addBodyFunction
import it.unibo.collektive.codegen.utils.KotlinPoetUtils.addSingleStatementBodyFunction
import it.unibo.collektive.codegen.utils.KTypeUtils.toTypeNameWithRecurringGenericSupport
import it.unibo.collektive.codegen.utils.KTypeUtils.toTypeVariableName
import it.unibo.collektive.codegen.utils.KTypeUtils.getAllTypeVariables
import kotlin.math.pow
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

internal val FIELD_INTERFACE = Field::class.asClassName()
internal val FIELD_COMPANION = Field.Companion::class.asClassName()
internal val CHECK_ALIGNED = FIELD_COMPANION.member("checkAligned")
internal val FIELD_MAP = FIELD_INTERFACE.member("map")
internal val FIELD_MAP_WITH_ID = FIELD_INTERFACE.member("mapWithId")
internal val ID_BOUNDED_TYPE = TypeVariableName("ID", Any::class.asTypeName())
internal val operatorNotReturningField = listOf("compareTo", "contains")

internal fun ParameterSpec.isField() = type.toString().contains("Field")

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

internal fun generateFunction(callable: KCallable<*>, paramList: List<ParameterSpec>): FunSpec {
    return FunSpec.builder(callable.name).apply {
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
            1 -> addSingleStatementBodyFunction(callable)
            else -> addBodyFunction(callable, paramList, paramList.first().isField())
        }
    }.build()
}

internal fun KParameter.isFunctionType(): Boolean = (type.classifier as? KClass<*>)?.qualifiedName
    ?.startsWith("kotlin.Function")
    ?: false

/**
 * This function returns true if given a list of parameters of a [KCallable],
 * the receiver parameter is of the same type of given [callable] callable.
 *
 * For example: given a list of [ParameterSpec] composed of [String, Any?] and the [KCallable]
 * `fun String.plus(other: Any?): String`, the function will return true since the receiver
 * is of type [String] (the same as the first parameter of the [ParameterSpec] list).
 */
internal fun List<ParameterSpec>.isReceiverTheBaseClass(callable: KCallable<*>): Boolean =
    this.firstOrNull()?.type == callable.parameters.firstOrNull()?.type?.asTypeName()

/**
 * This function generates all the possible functions for a given [origin] callable.
 * The function will generate all the possible combinations of parameters where each parameter is replaced by a `Field`
 * of the same type.
 *
 * This function will exclude the generation of the function having the non-fielded version as receiver since
 * it will be shadowed by the original version of the function.
 *
 * For example: `fun String.plus(other: Any?): String` will not generate the function
 * `fun String.plus(other: Field<ID, Any?>): Field<ID, String>` since it will be shadowed by the original version.
 */
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
    val willShadowFieldedVersion = origin.parameters.any { it.type.isSupertypeOf(typeOf<Field<*, *>>()) }
    return parameterCombinations(functionArguments)
        // The shadow occurs if willShadow is true and the first argument of the generated function is the same as the
        // original callable receiver.
        .filterNot { willShadowFieldedVersion && it.isReceiverTheBaseClass(origin) }
        .map { paramList -> generateFunction(origin, paramList) }
}

internal fun generatePrimitivesFile(origin: List<KCallable<*>>, packageName: String, fileName: String): FileSpec? {
    // If one of the KCallable is a potential supertype of the corresponding "fielded version" should be excluded
    // since it will be shadowed by the non-fielded version.
    val functions = origin.flatMap { generateFunctions(it) }
    if (functions.isEmpty()) return null
    val objectContainer = TypeSpec.objectBuilder(fileName).apply {
        functions.forEach {
            addFunction(it)
        }
    }.build()
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
        addType(objectContainer)
    }.build()
}

val autoConversions = mapOf(
    "java.lang.Appendable" to "kotlin.text.Appendable",
    "java.util.Comparator" to "kotlin.Comparator",
    "java.util.HashSet" to "kotlin.collections.HashSet",
).mapValues { (_, kotlinClass) -> ClassName.bestGuess(kotlinClass) }
