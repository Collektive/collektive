import it.unibo.collektive.aggregate.api.Aggregate

fun Aggregate<Int>.exampleAggregate() {}

// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========
// END OF BOILERPLATE CODE
// ========= ========= ========= ========= ========= ========= ========= ========= ========= =========

fun entry() {
  for(i in 1..3) {
    aggregate {
      neighboring(0)
    }
  }
}