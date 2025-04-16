package it.unibo.collektive.collektivize.utils

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.STAR
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KTypeProjection
import kotlin.reflect.KVariance

/**
 * Utilities for interoperate with [KType].
 */
object KTypeUtils {
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
    internal fun TypeName.getAllTypeVariables(): List<TypeVariableName> = when (this) {
        is TypeVariableName -> listOf(TypeVariableName(this.name, this.bounds, null))
        is ParameterizedTypeName -> typeArguments.flatMap { it.getAllTypeVariables() }
        else -> emptyList()
    }

    private fun KVariance?.toKModifier() = when (this) {
        null -> null
        KVariance.INVARIANT -> null
        KVariance.IN -> KModifier.IN
        KVariance.OUT -> KModifier.OUT
    }

    internal fun KTypeParameter.toTypeVariableName(
        recurryingTypeArguments: Set<KTypeParameter> = emptySet(),
    ): TypeVariableName = when {
        this in recurryingTypeArguments ->
            TypeVariableName(
                name = name,
                variance = variance.toKModifier(),
            ).copy(reified = isReified)
        else -> {
            val unbound = TypeVariableName(name)
            val upperBounds =
                upperBounds
                    .filterNot { it.isMarkedNullable && it.classifier == Any::class }
                    .map {
                        it.toTypeNameWithRecurringGenericSupport(recurryingTypeArguments + this)
                    }
            unbound.copy(
                bounds = upperBounds,
                reified = isReified,
            )
        }
    }

    internal fun KTypeProjection.toTypeNameWithRecurringGenericSupport(
        recurringTypeArguments: Set<KTypeParameter> = emptySet(),
    ): TypeName = when (val result = type.toTypeNameWithRecurringGenericSupport(recurringTypeArguments)) {
        is TypeVariableName ->
            TypeVariableName(name = result.name, variance = variance.toKModifier()).copy(
                nullable = result.isNullable,
                annotations = result.annotations,
                reified = result.isReified,
            )
        else -> result
    }

    private fun TypeName.projected(): TypeName = when (this) {
        is TypeVariableName -> {
            when (variance) {
                KModifier.IN -> WildcardTypeName.consumerOf(this)
                KModifier.OUT -> WildcardTypeName.producerOf(this)
                else -> this
            }
        }
        else -> this
    }

    internal fun KType?.toTypeNameWithRecurringGenericSupport(
        recurringTypeArguments: Set<KTypeParameter> = emptySet(),
    ): TypeName {
        if (this == null) {
            return STAR
        }
        val classifier = classifier

        fun ClassName.parameterized(): TypeName = run {
            when {
                arguments.isEmpty() -> this
                specializedArrayTypes.contains(classifier) -> this // No type arguments expected for class '*Array'.
                else ->
                    parameterizedBy(
                        arguments.map {
                            it.toTypeNameWithRecurringGenericSupport(recurringTypeArguments).projected()
                        },
                    )
            }
        }
        return when (classifier) {
            is KClass<*> -> {
                when (val qualifiedName = classifier.qualifiedName) {
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
}
