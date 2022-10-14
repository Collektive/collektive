fun main() {

    val fields = Environment.localFields
    val f: (Int) -> Int = { it * 2 }
    fields.addField(f) //this will not be necessary in the future
    // DSL functions
    aggregate {
        neighbouring(f)
    }
}
