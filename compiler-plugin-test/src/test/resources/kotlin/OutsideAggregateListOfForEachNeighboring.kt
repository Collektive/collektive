import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.Collektive.Companion.aggregate

fun entry() {
  listOf(1,2,3).forEach {
    aggregate(0) {
      neighboring(0)
    }
  }
}