package it.unibo.collektive.codegen

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.FileSpec
import it.unibo.collektive.field.Field
import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.kotlinFunction


object FieldedMembersGenerator {

    val baseExtensions = sequenceOf(
        "Arrays"
    ).map {
        Class.forName("kotlin.collections.${it}Kt").kotlin
    }

    /**
     * The base types for which to generate field functions.
     */
    val baseTargetTypes = sequenceOf(
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
        Map::class,
        List::class,
        Collection::class,
        Set::class,
        Array::class,
        ByteArray::class,
        ShortArray::class,
        IntArray::class,
        FloatArray::class,
        DoubleArray::class,
        LongArray::class,
        // Other types
        Comparator::class,
        Pair::class,
        Triple::class,
        Result::class,
        // Function collections
    ) + baseExtensions

    val forbiddenReturnTypes: List<KType> = listOf<KClassifier>(
        Unit::class,
        Nothing::class,
        MutableList::class,
        MutableMap::class,
        MutableSet::class,
        MutableCollection::class,
        MutableIterator::class,
        MutableIterable::class,
        MutableListIterator::class,
    ).map { it.starProjectedType }

    val forbiddenPrefixes = listOf(
        "java",
        "javax",
    )

    val permanentlyExcludedMemberNames = listOf(
        "associateByTo",
        "associateTo",
        "associateWithTo",
        "clone",
        "dec",
        "filterIndexedTo",
        "filterNotTo",
        "fold",
        "foldIndexed",
        "foldIndexedOrNull",
        "foldLeft",
        "foldLeftIndexed",
        "foldOrNull",
        "foldRight",
        "foldRightIndexed",
        "inc",
        "joinTo",
        "reduce",
        "reduceIndexed",
        "reduceIndexedOrNull",
        "reduceLeft",
        "reduceLeftIndexed",
        "reduceOrNull",
        "reduceRight",
        "reduceRightIndexed",
        "readObject",
        "toMutableMap",
        "toMutableList",
        "toMutableSet",
        "writeObject",
    ) + listOf("fold", "reduce")
        .flatMap { listOf(it, "${it}Indexed", "${it}Left", "${it}Right", "${it}OrNull") }
        .flatMap { listOf(it, "${it}OrNull", "${it}Indexed") }
        .flatMap { listOf(it, "${it}OrNull") }

    /**
     * Generates field-based functions for the given types.
     */
    fun generateFieldFunctionsForTypes(
        types: Sequence<KClass<*>>,
        excludeMembers: List<String> = emptyList(),
    ): Sequence<FileSpec> {
        fun KCallable<*>.paramTypes() = parameters.drop(1).map { it.type }
        val forbiddenMembers = Field::class.members.map { member -> member.name to member.paramTypes() }
        // "dec" and "inc" are excluded due to: KT-24800
        val forbiddenMembersName = permanentlyExcludedMemberNames + excludeMembers
        return types.map { clazz ->
            val javaMethods: Collection<KCallable<*>> = clazz.java.methods.mapNotNull {
                runCatching { it.kotlinFunction }.getOrNull()
            }
            val kotlinMembers: Collection<KCallable<*>> = clazz.members
            val allTargets = javaMethods.toSet() + kotlinMembers.toSet()
            val order = compareBy<KCallable<*>> { it.name }
                .thenBy { it.parameters.size }
                .thenBy { it.paramTypes().joinToString() }
            val sortedTargets = allTargets.sortedWith(order)
            val notDeprecated = sortedTargets.filter { it.annotations.none { annotation -> annotation is Deprecated } }
            val public = notDeprecated.filter { it.visibility == KVisibility.PUBLIC }
            val returnTypeMeaningful = public.filterNot {
                forbiddenPrefixes.any { prefix -> it.returnType.toString().startsWith(prefix) } ||
                it.returnType in forbiddenReturnTypes
            }
            val parametersMeaningful = returnTypeMeaningful.filterNot { method ->
                method.parameters.any { parameter ->
                    forbiddenPrefixes.any { prefix ->  parameter.type.toString().startsWith(prefix) }
                }
            }
            val genericBoundsMeaningful = parametersMeaningful.filterNot { method ->
                method.typeParameters.any { typeParameter ->
                    forbiddenPrefixes.any { prefix ->
                        typeParameter.upperBounds.any { it.toString().startsWith(prefix) }
                    }
                }
            }
            val noConflictingMethods = genericBoundsMeaningful.filterNot { callable ->
                callable.name to callable.paramTypes() in forbiddenMembers
            }
            val validMembers = noConflictingMethods
                .filterNot { it.name in forbiddenMembersName }
                .toList()
            val name = checkNotNull(clazz.simpleName) {
                "Cannot generate field functions for anonymous class $clazz"
            }.removeSuffix("Kt")
            generatePrimitivesFile(
                validMembers,
                "it.unibo.collektive.primitives",
                "Fielded${name}${if (name.endsWith("s")) "Extensions" else 's'}",
            )
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val outputDir = if (args.isEmpty()) null else args[0]
        generateFieldFunctionsForTypes(baseTargetTypes).forEach { source ->
            when {
                outputDir == null -> println(source)
                else -> source.writeTo(File(outputDir))
            }
        }
    }
}
