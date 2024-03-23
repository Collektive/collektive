package it.unibo.collektive.codgen

import com.squareup.kotlinpoet.FileSpec
import it.unibo.collektive.field.Field
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.KVisibility

/**
 * The base types for which to generate field functions.
 */
val baseTargetTypes = sequenceOf(
    Boolean::class,
    Byte::class,
    Short::class,
    Int::class,
    Float::class,
    Double::class,
    Long::class,
    UByte::class,
    UShort::class,
    UInt::class,
    ULong::class,
    Char::class,
    CharSequence::class,
    String::class,
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
    // Comparator::class,
    Pair::class,
    Triple::class,
    Result::class,
    Throwable::class,
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
    val forbiddenMembersName = listOf("compareTo") + excludeMembers
    return types.map { clazz ->
        val membersToField = clazz.members.filterNot { it.name to it.paramTypes() in forbiddenMembers }
            .filter { it.annotations.isEmpty() }
            .filterNot { it.visibility == KVisibility.INTERNAL }
            .filterNot { it.name in forbiddenMembersName }
            .toList()
        generatePrimitivesFile(
            membersToField,
            "it.unibo.collektive.primitives",
            "Field${clazz.simpleName}Primitives",
        )
    }
}
