fun main() {
    val field = FieldImpl<Double>()
    println("Running on ${Platform.name}")

    // DSL functions
    aggregate {
        neighbouring()
    }
}
