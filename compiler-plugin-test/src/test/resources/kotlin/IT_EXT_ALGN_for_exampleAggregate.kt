import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate() {}

// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========
// END OF BOILERPLATE CODE
// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========

fun Aggregate<Int>.entry() {
    alignedOn(0) {
        for(i in 1..3) {
            exampleAggregate()
        }
    }
}