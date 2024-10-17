import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate() {}

// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========
// END OF BOILERPLATE CODE
// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========

fun entry() {
  for(i in 1..3) {
    aggregate(0) {
      neighboring(0)
    }
  }
}