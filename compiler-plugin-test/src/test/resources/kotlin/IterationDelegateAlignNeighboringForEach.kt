/**
 * This file has been auto-generated.
 * See [https://github.com/FreshMag/subjekt/blob/main/generator/src/main/resources/subjects/IterationWithAggregate.yaml](here)
 * for details.
*/
import it.unibo.collektive.aggregate.api.Aggregate
fun delegate(aggregate: Aggregate<Int>) {
  aggregate.alignedOn(0) {
    aggregate.neighboring(0)
  }
}

fun Aggregate<Int>.entry() {
  (1..3).forEach {
    delegate(this)
  }
}
