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
    val generatedFiles = targetTypes.map { clazz ->
        val membersToField = clazz.members.filterNot { it.name to it.paramTypes() in forbiddenMembers }
            .filter { it.annotations.isEmpty() }
            .filterNot { it.name in forbiddenMembersName }
            .toList()
        generatePrimitivesFile(
            membersToField,
            "it.unibo.collektive.primitives",
            "Field${clazz.simpleName}Primitives",
        )
    }

    generatedFiles.forEach {
        it.writeTo(File("primitives/src/main/resources/kotlin"))
    }
}
