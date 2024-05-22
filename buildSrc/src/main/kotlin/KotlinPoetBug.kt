import com.squareup.kotlinpoet.asTypeName
import kotlin.reflect.jvm.kotlinFunction

fun main(args: Array<String>) {
    Class.forName("kotlin.collections.ArraysKt").methods.first {
        it.name == "max" && it.parameters.first().parameterizedType.typeName.contains("Comparable")
    }.kotlinFunction?.parameters?.first()?.type?.asTypeName()
}
