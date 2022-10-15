fun main() {

    val fields = Environment.localFields
    val f: (Int) -> Int = { it * 2 }
    println(fields)
    // DSL functions
    aggregate {
        repeating(1, f)
    }
    println(fields)
}
