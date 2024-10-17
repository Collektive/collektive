import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate() {}

// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========
// END OF BOILERPLATE CODE
// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========

fun Aggregate<Int>.entry() {
   listOf(1,2,3).forEach {
        fun Aggregate<Int>.nested() {
            exampleAggregate()
        }
    }
}