/**
 * This file has been auto-generated.
 * See [https://github.com/FreshMag/collektive-plugin/blob/test/generated-cases/compiler-plugin-test/src/test/resources/yaml/IterationWithAggregate.yaml](here)
 * for details.
*/
import it.unibo.collektive.aggregate.api.Aggregate
fun Aggregate<Int>.entry() {
  (1..3).forEach {
    fun Aggregate<Int>.nested() {
      neighboring(0)
    }
  }
}
