import it.unibo.collektive.aggregate.api.Aggregate
import it.unibo.collektive.Collektive.Companion.aggregate

fun Aggregate<Int>.entry() {
    alignedOn(0) {
        for(i in 1..3) {
            neighboring(0)
        }
    }
}