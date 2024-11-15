/**
 * This file has been auto-generated.
 * See [https://github.com/FreshMag/subjekt/blob/main/generator/src/main/resources/subjects/IterationWithAggregate.yaml](here)
 * for details.
*/
import it.unibo.collektive.Collektive.Companion.aggregate

fun entry() {
  (1..3).forEach {
    aggregate(0) {
      neighboring(0)
    }
  }
}
