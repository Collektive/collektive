/**
 * This file has been auto-generated.
 * See [https://github.com/FreshMag/collektive-plugin/blob/test/generated-cases/compiler-plugin-test/src/test/resources/yaml/IterationWithAggregate.yaml](here)
 * for details.
*/
import it.unibo.collektive.aggregate.api.Aggregate
fun delegate(aggregate: Aggregate<Int>) {
  aggregate.neighboring(0)
}

fun Aggregate<Int>.entry() {
  (1..3).forEach {
    delegate(this)
  }
}
