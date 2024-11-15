/**
 * This file has been auto-generated.
 * See [https://github.com/FreshMag/collektive-plugin/blob/test/generated-cases/compiler-plugin-test/src/test/resources/yaml/IterationWithAggregate.yaml](here)
 * for details.
*/
import it.unibo.collektive.aggregate.api.Aggregate
fun Aggregate<Int>.entry() {
  alignedOn(0) {
    for (i in 1..3) {
      neighboring(0)
    }
  }
}
