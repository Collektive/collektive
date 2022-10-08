fun main() {
    println("Running on ${Platform.name}")
    val fields = Fields<Any>()

    val f: (Int) -> Int = { it * 2 }
    fields.addField(f)
    fields.addField(String)

    val field = fields.retrieveField(String)
    field.addElement(0, "test")

    println(fields.retrieveField(String))

    // DSL functions
    aggregate {

    }
}
