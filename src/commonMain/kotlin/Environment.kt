import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Environment {
    const val deviceId: Int = 0 // For test purposes
    val fields: Fields<Any> = Fields()
}