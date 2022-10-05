fun main() {
    val field = FieldImpl<Int>()
    println("Running on ${Platform.name}")
    field.print()
    aggregate {
        neighbouring()
    }
}
