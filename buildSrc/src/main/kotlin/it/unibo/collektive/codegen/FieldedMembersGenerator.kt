package it.unibo.collektive.codegen

import com.squareup.kotlinpoet.FileSpec
import it.unibo.collektive.field.Field
import java.io.File
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility


object FieldedMembersGenerator {

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
    )

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
        val forbiddenMembersName = listOf("compareTo", "clone", "dec", "inc") + excludeMembers
        return types.map { clazz ->
            val membersToField = clazz.members.asSequence()
                .filter { it.annotations.none { annotation -> annotation is Deprecated } }
                .filterNot { it.name to it.paramTypes() in forbiddenMembers }
                .filter { it.annotations.isEmpty() }
                .filterNot { it.visibility == KVisibility.INTERNAL }
                .filterNot { it.name in forbiddenMembersName }
                .toList()
            generatePrimitivesFile(
                membersToField,
                "it.unibo.collektive.primitives",
                "Fielded${clazz.simpleName}s",
            )
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        val outputDir = args[0]
        generateFieldFunctionsForTypes(baseTargetTypes).forEach {
            it.writeTo(File(outputDir))
        }
    }
}
