fun main() {

    val f: (Int) -> Int = { it * 2 }
    //val k: (String) -> Int = { it.length }
    // DSL functions
    aggregate {
        repeating(1, f)
        //neighbouring(f(1))
        //neighbouring(k("test"))
    }
}
