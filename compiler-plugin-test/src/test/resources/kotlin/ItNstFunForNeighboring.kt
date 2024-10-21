import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.Collektive.Companion.aggregate

fun Aggregate<Int>.entry() {
   for(i in 1..3) {
        fun Aggregate<Int>.nested() {
            neighboring(0)
        }
    }
}