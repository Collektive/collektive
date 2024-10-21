import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.Collektive.Companion.aggregate

fun Aggregate<Int>.entry() {
   listOf(1,2,3).forEach {
        fun Aggregate<Int>.nested() {
            neighboring(0)
        }
    }
}