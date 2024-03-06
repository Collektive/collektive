package it.unibo.collektive.codgen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KOperator
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.asTypeVariableName
import com.squareup.kotlinpoet.typeNameOf
import kotlin.reflect.KCallable
import kotlin.reflect.KType

/**
 * TODO.
 */
class FieldToFieldOperations : CollektiveCodeGenerator<List<KType>> {
    override fun generate(input: List<KType>, packageName: String, fileName: String): FileSpec {
        val fileBuilder = FileSpec.builder(packageName, fileName)
        val fieldFunctionTemplate = FunSpec.builder(MemberName(packageName, KOperator.PLUS))
            .addTypeVariable(ID_BOUNDED_TYPE)
            .receiver(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, INT_TYPE))
            .addParameter("other", FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, INT_TYPE))
            .addStatement("return alignedMap(other) { a, b -> a + b }")
            .build()
        fileBuilder.addFunction(fieldFunctionTemplate)
        return fileBuilder.build()
    }

    companion object {
        private val FIELD_INTERFACE = ClassName("it.unibo.collektive.field", "Field")
        private val ANY_TYPE = ClassName("kotlin", "Any")
        private val INT_TYPE = typeNameOf<Int>()
        private val ID_BOUNDED_TYPE = TypeVariableName("ID", ANY_TYPE)

        /**
         * TODO.
         */
        fun generateFun(origin: KCallable<*>): String {
            val functionParameters = origin.parameters.drop(1).map {
                ParameterSpec(it.name ?: error("No name found"), it.type.asTypeName())
            }
            val functionSpecification = FunSpec.builder(origin.name).apply {
                addTypeVariable(ID_BOUNDED_TYPE)
                addTypeVariables(origin.typeParameters.map { it.asTypeVariableName() })
                addParameters(functionParameters)
                receiver(FIELD_INTERFACE.parameterizedBy(ID_BOUNDED_TYPE, origin.parameters[0].type.asTypeName()))
                when (functionParameters.size) {
                    0 -> {
                        addStatement("val result = map { it.${origin.name}() }")
                        addStatement("return result")
                    }
                    1 -> {
                        val firstArgument = functionParameters[0].name
                        addStatement("val result = alignedMap($firstArgument) { a, b -> a.${origin.name}(b) }")
                        addStatement("return result")
                    }
                }
            }.build()
            return functionSpecification.toString()
        }
    }
}

/**
 * TODO.
 */
fun main() {
    Int::class.members.forEach { println(FieldToFieldOperations.generateFun(it)) }

    val fieldToFieldOperations = FieldToFieldOperations()
    val fileSpec = fieldToFieldOperations.generate(
        emptyList(),
        "it.unibo.collektive.codgen",
        "FieldToFieldOperations",
    )
    println(fileSpec)
}
