fun main() {
    fun AggregateContext.compute() = neighbouring("test")

    aggregate {
        compute()
    }
}
