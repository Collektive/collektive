import kotlin.native.concurrent.ThreadLocal

@ThreadLocal
object Fields {
    private val fields: MutableList<Field<Any>> = mutableListOf()

    fun retrieveAllFields(): MutableList<Field<Any>> = fields

    fun <X : Any> addField(event: Field<X>): Boolean = fields.add(event)
}
