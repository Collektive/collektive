import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName

fun <T> foo(): Array<out T> = TODO()

fun main() {
    val funspec = FunSpec.builder("foo")
        .addTypeVariable(TypeVariableName("T"))
        .returns(
            ClassName.bestGuess("kotlin.Array")
                .parameterizedBy(WildcardTypeName.producerOf(TypeVariableName("T")))
        )
        .addStatement("TODO()")
        .build()
    println(
        FileSpec.builder("foo.bar", "Baz")
        .addFunction(funspec)
        .build()
        .toString()
    )
}
