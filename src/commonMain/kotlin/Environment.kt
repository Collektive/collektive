import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Environment {
    val fields: Fields<Any> = Fields()
}