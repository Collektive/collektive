package it.unibo.collektive.codgen

import it.unibo.collektive.codgen.utils.generatePrimitivesFile
import it.unibo.collektive.field.Field
import java.io.File
import kotlin.reflect.KCallable

/**
 * TODO.
 */
fun main() {
    val targetTypes = sequenceOf(
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
        //        Comparator::class,
        Pair::class,
        Triple::class,
        Result::class,
        Throwable::class,
    )

    fun KCallable<*>.paramTypes() = parameters.drop(1).map { it.type }
    val forbiddenMembers = Field::class.members.map { member -> member.name to member.paramTypes() }
    val forbiddenMembersName = listOf("compareTo")
    val origins = targetTypes.flatMap { it.members }
        .filterNot { it.name to it.paramTypes() in forbiddenMembers }
        .filterNot { it.annotations.any { c -> c.annotationClass == Deprecated::class } }
        .filterNot { it.name in forbiddenMembersName }
        .toList()

    generatePrimitivesFile(origins).writeTo(File("primitives/src/main/resources/kotlin"))
}
