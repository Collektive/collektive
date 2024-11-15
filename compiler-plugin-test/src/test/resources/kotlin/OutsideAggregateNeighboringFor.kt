/**
 * This file has been auto-generated.
 * See [https://github.com/FreshMag/subjekt/blob/main/generator/src/main/resources/subjects/IterationWithAggregate.yaml](here)
 * for details.
*/
import it.unibo.collektive.Collektive.Companion.aggregate

fun entry() {
  for (i in 1..3) {
    aggregate(0) {
      neighboring(0)
    }
  }
}
