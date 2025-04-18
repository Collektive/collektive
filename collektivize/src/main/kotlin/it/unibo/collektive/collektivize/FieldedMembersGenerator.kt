/*
 * Copyright (c) 2025, Danilo Pianini, Nicolas Farabegoli, Elisa Tronetti,
 * and all authors listed in the `build.gradle.kts` and the generated `pom.xml` file.
 *
 * This file is part of Collektive, and is distributed under the terms of the Apache License 2.0,
 * as described in the LICENSE file in this project's repository's top directory.
 */

package it.unibo.collektive.collektivize

import com.squareup.kotlinpoet.FileSpec
import it.unibo.collektive.aggregate.Field
import it.unibo.collektive.collektivize.utils.generatePrimitivesFile
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.KTypeParameter
import kotlin.reflect.KVisibility
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.jvm.kotlinFunction
import kotlin.reflect.typeOf

/**
 * Utilities for fielded members.
 */
object FieldedMembersGenerator {
    /**
     * Base classes to inspect.
     */
    val baseExtensions =
        sequenceOf(
            "Arrays",
            "Collections",
            "Sets",
            "Maps",
        ).map {
            Class.forName("kotlin.collections.${it}Kt").kotlin
        }

    /**
     * The base types for which to generate field functions.
     */
    val baseTargetTypes =
        sequenceOf(
            // Boolean
            Boolean::class,
            // Numeric types
            Byte::class,
            Short::class,
            Int::class,
            Float::class,
            Double::class,
            Long::class,
            // Unsigned types
            UByte::class,
            UShort::class,
            UInt::class,
            ULong::class,
            // Chars and strings
            Char::class,
            CharSequence::class,
            String::class,
            // Collections
            Collection::class,
            Iterable::class,
            List::class,
            Map::class,
            Sequence::class,
            Set::class,
            // Other types
            Comparator::class,
            Pair::class,
            Triple::class,
            Result::class,
            // Function collections
        ) + baseExtensions

    private val mutablesAndSideEffects: List<KType> =
        listOf(
            typeOf<Array<*>>(),
            typeOf<MutableCollection<*>>(),
            typeOf<MutableCollection<*>>(),
            typeOf<MutableIterable<*>>(),
            typeOf<MutableIterator<*>>(),
            typeOf<MutableList<*>>(),
            typeOf<MutableListIterator<*>>(),
            typeOf<MutableMap<*, *>>(),
            typeOf<ShortArray>(),
            typeOf<IntArray>(),
            typeOf<FloatArray>(),
            typeOf<DoubleArray>(),
            typeOf<LongArray>(),
            typeOf<MutableSet<*>>(),
            typeOf<Unit>(),
        )

    private fun KType.isMutable(): Boolean = mutablesAndSideEffects.any { isSubtypeOf(it) }

    private fun KTypeParameter.isMutable() = upperBounds.any { bound -> bound.isMutable() }

    private val forbiddenPrefixes =
        listOf(
            "java",
            "javax",
        )

    private val permanentlyExcludedMemberNames =
        listOf(
            "associateByTo",
            "associateTo",
            "associateWithTo",
            "binarySearch",
            "clone",
            "contains",
            "containsValue",
            "copyInto",
            "dec",
            "filterIndexedTo",
            "filterIsInstanceTo",
            "filterNotNullTo",
            "filterNotTo",
            "filterTo",
            "flatMapTo",
            "fold",
            "foldIndexed",
            "foldIndexedOrNull",
            "foldLeft",
            "foldLeftIndexed",
            "foldOrNull",
            "foldRight",
            "foldRightIndexed",
            "groupByTo",
            "inc",
            "joinTo",
            "listOf",
            "listOfNotNull",
            "map",
            "mapIndexedNotNullTo",
            "mapIndexedTo",
            "mapNotNullTo",
            "mapOf",
            "mapTo",
            "reduce",
            "reduceIndexed",
            "reduceIndexedOrNull",
            "reduceLeft",
            "reduceLeftIndexed",
            "reduceOrNull",
            "reduceRight",
            "reduceRightIndexed",
            "readObject",
            "setOf",
            "setOfNotNull",
            "toBooleanArray",
            "toByteArray",
            "toCharArray",
            "toCollection",
            "toDoubleArray",
            "toFloatArray",
            "toIntArray",
            "toLongArray",
            "toShortArray",
            "toMutableMap",
            "toMutableList",
            "toMutableSet",
            "toTypedArray",
            "writeObject",
        ) +
            listOf("fold", "reduce")
                .flatMap { listOf(it, "${it}Indexed", "${it}Left", "${it}Right", "${it}OrNull") }
                .flatMap { listOf(it, "${it}OrNull", "${it}Indexed") }
                .flatMap { listOf(it, "${it}OrNull") }

    /**
     * Generates field-based functions for the given types.
     */
    @Suppress("CyclomaticComplexMethod")
    fun generateFieldFunctionsForTypes(
        types: Sequence<KClass<*>>,
        packageName: String = "it.unibo.collektive.stdlib",
        excludeMembers: List<String> = emptyList(),
    ): Sequence<FileSpec> {
        fun KCallable<*>.paramTypes() = parameters.drop(1).map { it.type }
        val forbiddenMembers = Field::class.members.map { member -> member.name to member.paramTypes() }
        // "dec" and "inc" are excluded due to: KT-24800
        val forbiddenMembersName = permanentlyExcludedMemberNames + excludeMembers
        return types.flatMap { clazz ->
            val javaMethods: Collection<KCallable<*>> =
                clazz.java.methods.mapNotNull {
                    runCatching { it.kotlinFunction }.getOrNull()
                }
            val kotlinMembers: Collection<KCallable<*>> = clazz.members
            val allTargets = javaMethods.toSet() + kotlinMembers.toSet()
            val order =
                compareBy<KCallable<*>> { it.name }
                    .thenBy { it.parameters.size }
                    .thenBy { it.paramTypes().joinToString() }
            val sortedTargets = allTargets.sortedWith(order)
            val withValidAnnotations =
                sortedTargets.filter { callable ->
                    callable.annotations.none {
                        it is Deprecated || it.annotationClass.simpleName == "PlatformDependent"
                    }
                }
            val public = withValidAnnotations.filter { it.visibility == KVisibility.PUBLIC }
            val returnTypeMeaningful =
                public.filterNot {
                    forbiddenPrefixes.any { prefix -> it.returnType.toString().startsWith(prefix) } ||
                        mutablesAndSideEffects.any { mutable -> it.returnType.isSubtypeOf(mutable) }
                }
            val parametersMeaningful =
                returnTypeMeaningful.filterNot { method ->
                    method.parameters.any { parameter ->
                        forbiddenPrefixes.any { prefix -> parameter.type.toString().startsWith(prefix) } ||
                            when (val classifier = parameter.type.classifier) {
                                null -> error("Null classifier in $method")
                                is KClass<*> -> parameter.type.isMutable()
                                is KTypeParameter -> classifier.isMutable()
                                else -> error("Unknown classifier type ${classifier::class}")
                            }
                    }
                }
            val genericBoundsMeaningful =
                parametersMeaningful.filterNot { method ->
                    method.typeParameters.any { typeParameter ->
                        forbiddenPrefixes.any { prefix ->
                            typeParameter.upperBounds.any { it.toString().startsWith(prefix) }
                        }
                    }
                }
            val noConflictingMethods =
                genericBoundsMeaningful.filterNot { callable ->
                    callable.name to callable.paramTypes() in forbiddenMembers
                }
            val validMembers =
                noConflictingMethods
                    .filterNot { it.name in forbiddenMembersName }
                    .toList()
            val name =
                checkNotNull(clazz.simpleName) {
                    "Cannot generate field functions for anonymous class $clazz"
                }.removeSuffix("Kt")
            val extensions: Map<String, List<KCallable<*>>> =
                validMembers.groupBy { callable ->
                    fun KType?.cleanString(): String = toString().substringBefore('<').substringAfterLast('.')

                    fun KTypeParameter.allUpperBounds(): List<KType> = upperBounds
                        .flatMap { (it.classifier as? KTypeParameter)?.allUpperBounds() ?: listOf(it) }
                    val receiver = callable.extensionReceiverParameter?.type
                    when (val classifier = receiver?.classifier) {
                        null -> name
                        is KTypeParameter ->
                            classifier
                                .allUpperBounds()
                                .map { it.cleanString() }
                                .sorted()
                                .joinToString(separator = "")
                        else -> receiver.cleanString()
                    }
                }
            extensions
                .asSequence()
                .filter { (_, members) -> members.isNotEmpty() }
                .mapNotNull { (receiver, members) ->
                    generatePrimitivesFile(
                        members,
                        "$packageName.${receiver.lowercase()}s",
                        "Fielded${name}${if (name.endsWith("s")) "Extensions" else 's'}",
                    )
                }
        }
    }
}
