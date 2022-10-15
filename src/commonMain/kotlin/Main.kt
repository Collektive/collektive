fun main() {

    val fields = Environment.localFields
    val f: (Int) -> Int = { it * 2 }
    val k: (String) -> Int = { it.length }
    println(fields)
    // DSL functions
    aggregate {
        repeating(1, f)
        repeating(1, f)
        repeating("casa", k)
    }
    println(fields)
}
