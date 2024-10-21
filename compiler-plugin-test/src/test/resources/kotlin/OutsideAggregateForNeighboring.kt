import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.Collektive.Companion.aggregate

fun entry() {
  for(i in 1..3) {
    aggregate(0) {
      neighboring(0)
    }
  }
}