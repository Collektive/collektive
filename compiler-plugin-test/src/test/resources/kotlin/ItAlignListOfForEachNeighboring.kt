import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.entry() {
    listOf(1,2,3).forEach {
        alignedOn(0) {
            neighboring(0)
        }
    }
}